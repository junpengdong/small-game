package com.activity.smallgame.service;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import com.activity.smallgame.model.mongodb.MiningArea;
import com.activity.smallgame.model.mongodb.UserProfile;
import com.activity.smallgame.model.mongodb.UserWallet;
import com.activity.smallgame.response.UserProfileResponse;
import com.activity.smallgame.socket.NettyWebSocket;
import com.activity.smallgame.utils.Encrypt;
import com.activity.smallgame.utils.JsonUtils;
import com.activity.smallgame.utils.ProveUtil;
import com.activity.smallgame.utils.SettlementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yeauty.pojo.Session;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Mr.dong
 * @create: 2019-08-20 20:18
 **/
@Service
public class MiningService {

    private Logger logger = LoggerFactory.getLogger(MiningService.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private MiningRecordService miningRecordService;

    @Autowired
    private MiningAreaService miningAreaService;

    @Autowired
    private NettyWebSocket nettyWebSocket;

    @Autowired
    private StringRedisTemplate redis;

    /**
     * 根据用户编码创建线程
     * @param userCode
     * @return
     */
    public ExecutorService createExecutorService(String userCode) {
        ExecutorService executorService = MiningConstant.executorMap.get(userCode);
        if (executorService != null) {
            logger.info("SMGAME日志消息 --> 用户编码为：" + userCode + " 已存在线程, 当前Mining线程数量：" + MiningConstant.executorMap.size());
            return executorService;
        }
        executorService = Executors.newCachedThreadPool();
        // 存放用户对应开启的线程
        MiningConstant.executorMap.put(userCode, executorService);
        // 存放线程状态
        MiningConstant.executorStatusMap.put(executorService, false);
        logger.info("SMGAME日志消息 --> 用户编码为：" + userCode + " 创建新线程，当前Mining线程数量：" + MiningConstant.executorMap.size());
        return executorService;
    }

    public void startMining(String userCode, ExecutorService executorService) {
        executorService.execute(() -> {
            try {
                UserProfile userProfile = userProfileService.getUserProfileByUserCode(userCode);
                MiningArea miningArea;
                if (null == userProfile.getUseAreaCode())  {
                    miningArea = miningAreaService.getFirstMiningArea();
                    userProfile.setUseAreaCode(miningArea.getAreaCode());
                    userProfileService.updateUserProfile(userProfile);
                }else {
                    miningArea = miningAreaService.getMiningAreaByAreaCode(userProfile.getUseAreaCode());
                }
                MiningConstant.miningAreaMap.put(userCode, miningArea);
                // 矿区矿的余量为0
                if (!checkMiningSurplus(userCode, miningArea)) {
                    executorService.shutdown();
                    logger.info("SMGAME日志消息 --> 用户：{" + userCode + "}所选择的矿区：{" + miningArea.getAreaName() + "}矿量不足，已自动停止挖矿。");
                    return;
                }
                miningAlgorithm(userCode, executorService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 挖矿算法
     * @param userCode
     * @param executorService
     * @throws Exception
     */
    private void miningAlgorithm(String userCode, ExecutorService executorService) throws Exception {
        UserWallet userWallet = userWalletService.getUserWalletByUserCode(userCode);
        if (null == userWallet) {
            executorService.shutdown();
            throw new SmallGameException(ResultEnum.USER_WALLET_NOT_EXIST);
        }
        // 计算总数
        long calculationCount = 0;
        String prove = ProveUtil.getRandomProve(10);
        while (true) {
            calculationCount += 1;
            Long time = System.currentTimeMillis() + (long) (Math.random()*10);
            String encryptStr = Encrypt.getMD5(time.toString());
            int result = compareStr(prove, encryptStr);
            if (result >= 4) {
                UserProfile userProfile = userProfileService.getUserProfileByUserCode(userCode);
                MiningArea miningArea = MiningConstant.miningAreaMap.get(userCode);
                if (!checkMiningSurplus(userCode, miningArea)) {
                    logger.info("SMGAME日志消息 --> 用户：{" + userCode + "}所选择的矿区：{" + miningArea.getAreaName() + "}矿量不足，已自动停止挖矿。");
                    executorService.shutdown();
                    return;
                }

                addCoin(userProfile, userWallet, calculationCount, result, miningArea);

                // 初始化值
                calculationCount = 0;
                prove = ProveUtil.getRandomProve(10);
                Thread.sleep(500);
            }
        }
    }

    /**
     * 添加矿币
     * @param userProfile
     * @param userWallet
     * @param calculationCount
     * @param result
     */
    private void addCoin(UserProfile userProfile, UserWallet userWallet, long calculationCount, int result, MiningArea miningArea) {
        Double coin = SettlementUtil.settleByProof(calculationCount, userProfile.getMultiple());
        String userCode = userProfile.getUserCode();
        Boolean isAuth = MiningConstant.authStatusMap.get(userCode);
        if (null == isAuth || !isAuth) {
            coin = coin * 0.7;
//            logger.info("SMGAME日志消息 --> 用户：{" + userCode + "}产生新的矿币，算法匹配长度：{" + result +"}，但处于离线状态，数量*0.7：{" + coin + "}");
        }else {
//            logger.info("SMGAME日志消息 --> 用户：{" + userCode + "}产生新的矿币，算法匹配长度：{" + result +"}，数量：{" + coin + "}");
        }

        if (miningArea.getSurplus() < coin) {
            coin = miningArea.getSurplus();
            miningArea.setSurplus(0D);
        }else {
            miningArea.setSurplus(miningArea.getSurplus() - coin);
        }
        int addCoinResult = result;
        switch (result) {
            case 4:
                String cacheKey = String.format(MiningConstant.CACHE_RESULT_FIVE_COUNT, userCode);
                String cacheValue = redis.opsForValue().get(cacheKey);
                if (StringUtils.isEmpty(cacheValue) || Integer.valueOf(cacheValue) < 10) {
                    int count = StringUtils.isEmpty(cacheValue) ? 1 : Integer.valueOf(cacheValue) + 1;
                    redis.opsForValue().set(cacheKey, String.valueOf(count));
                    userWallet.setCommonCoin(userWallet.getCommonCoin() + coin);
                }else {
                    redis.opsForValue().set(cacheKey, String.valueOf(0));
                    userWallet.setSeniorCoin(userWallet.getSeniorCoin() + coin);
                    addCoinResult = 5;
                }
                break;
            case 5:
                Boolean addFew = String.valueOf(System.currentTimeMillis()).endsWith("00");
                Boolean addRare = String.valueOf(System.currentTimeMillis()).endsWith("99");
                if (addFew) {
                    userWallet.setFewCoin(userWallet.getFewCoin() + coin);
                    addCoinResult = 7;
                }else if (addRare){
                    userWallet.setRareCoin(userWallet.getRareCoin() + coin);
                    addCoinResult = 6;
                }else {
                    userWallet.setSeniorCoin(userWallet.getSeniorCoin() + coin);
                    addCoinResult = 5;
                }
                break;
            default:
                addCoinResult = 7;
                userWallet.setFewCoin(userWallet.getFewCoin() + coin);
                break;
        }
        logger.info("SMGAME日志消息 --> 用户：{" + userCode + "}产生新的矿币，算法匹配长度：{" + result +"}，最后添加结果：{" + addCoinResult + "}，数量：{" + coin + "}");
        userWalletService.updateUserWallet(userCode, userWallet);
        miningRecordService.addMiningRecord(userCode, coin, addCoinResult);
        miningAreaService.updateMiningArea(miningArea);

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserCode(userCode);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userProfileResponse.setCreateTime(format.format(userProfile.getCreateTime()));
        userProfileResponse.setCommonCoin(userWallet.getCommonCoin());
        userProfileResponse.setSeniorCoin(userWallet.getSeniorCoin());
        userProfileResponse.setRareCoin(userWallet.getRareCoin());
        userProfileResponse.setFewCoin(userWallet.getFewCoin());
        // 发送用户详情到客户端
        nettyWebSocket.toMessageAndSend(ResultEnum.SK_USER_PROFILE.getCode(), ResultEnum.SK_USER_PROFILE.getMsg(), JsonUtils.toJson(userProfileResponse), MiningConstant.sessionMap.get(userCode));
    }

    /**
     * 检测矿区矿量余量
     * @param userCode
     * @param miningArea
     */
    private Boolean checkMiningSurplus(String userCode, MiningArea miningArea) {
        if (miningArea.getSurplus() == 0) {
            Session session = MiningConstant.sessionMap.get(userCode);
            nettyWebSocket.toMessageAndSend(ResultEnum.MINING_AREA_SURPLUS_ZERO.getCode(), ResultEnum.MINING_AREA_SURPLUS_ZERO.getMsg(), null, session);
            return false;
        }
        return true;
    }

    private int compareStr(String prove, String encryptStr) {
        int count = 0;
        for (int i = 0; i < encryptStr.length(); i++) {
            if (encryptStr.charAt(i) == prove.charAt(i)) {
                count += 1;
            }else {
                break;
            }
        }
        return count;
    }
}
