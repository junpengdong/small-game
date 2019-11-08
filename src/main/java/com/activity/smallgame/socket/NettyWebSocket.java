package com.activity.smallgame.socket;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.dto.MessageDto;
import com.activity.smallgame.dto.request.MessageDtoRequest;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.lock.RedisLock;
import com.activity.smallgame.model.mongodb.UserProfile;
import com.activity.smallgame.model.mongodb.UserWallet;
import com.activity.smallgame.response.SocketResponse;
import com.activity.smallgame.response.UserProfileResponse;
import com.activity.smallgame.service.MiningService;
import com.activity.smallgame.service.UserProfileService;
import com.activity.smallgame.service.UserWalletService;
import com.activity.smallgame.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: Mr.dong
 * @create: 2019-08-19 14:50
 **/
@ServerEndpoint(prefix = "netty-websocket")
@Component
public class NettyWebSocket {

    private Logger logger = LoggerFactory.getLogger(NettyWebSocket.class);

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private MiningService miningService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserProfileService userProfileService;

    private static final String CACHE_OPEN_REDIS_LOCK = "cache.open.redis.lock.%s";

    private static final String CACHE_CLOSE_REDIS_LOCK = "cache.close.redis.lock.%s";

    private static final Integer LOCK_TIME_OUT = 5 * 1000;

    @OnOpen
    public void onOpen(Session session, ParameterMap parameterMap) throws IOException {
        String userCode = parameterMap.getParameter("userCode");
        if (StringUtils.isEmpty(userCode)) {
            toMessageAndSend(ResultEnum.ERROR_PARAMS.getCode(), ResultEnum.ERROR_PARAMS.getMsg(), null, session);
            return;
        }
        // 加锁
        String cacheKey = String.format(CACHE_OPEN_REDIS_LOCK, userCode);
        Long lockTime = System.currentTimeMillis() + LOCK_TIME_OUT;
        try {
            if (redisLock.lock(cacheKey, String.valueOf(lockTime))) {
                redis.opsForHash().delete(MiningConstant.CACHE_DISCONNECT_USER, userCode);
                startMining(session, userCode);
                // 发送用户详情到客户端
                toMessageAndSend(ResultEnum.SK_USER_PROFILE.getCode(), ResultEnum.SK_USER_PROFILE.getMsg(), JsonUtils.toJson(toUserProfileResponse(userCode)), MiningConstant.sessionMap.get(userCode));
            }
        }catch (Exception e) {
            logger.error("NettyWebSocket onOpen exception：", e);
        }finally {
            redisLock.unlock(cacheKey, String.valueOf(lockTime));
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String userCode = MiningConstant.userCodeMap.get(session.id().asShortText());
        if (StringUtils.isEmpty(userCode)) {
            return;
        }
        String cacheKey = String.format(CACHE_CLOSE_REDIS_LOCK, userCode);
        Long lockTime = System.currentTimeMillis() + LOCK_TIME_OUT;
        try {
            if (redisLock.lock(cacheKey, String.valueOf(lockTime))) {
                // 断开连接后5分钟内不登录，则将授权状态改为false
                redis.opsForHash().putIfAbsent(MiningConstant.CACHE_DISCONNECT_USER, userCode, "0");
                initData(userCode);
            }
        }catch (Exception e) {
            logger.error("NettyWebSocket onClose exception：", e);
        }finally {
            redisLock.unlock(cacheKey, String.valueOf(lockTime));
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("NettyWebSocket onError: ", throwable);
        String userCode = MiningConstant.userCodeMap.get(session.id().asShortText());
        initData(userCode);
        throwable.printStackTrace();
    }

    @OnMessage
    public void OnMessage(Session session, String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }

        MessageDto messageDto = (MessageDto) JsonUtils.fromJson(message, new TypeToken<MessageDto>(){}.getType());
        if (!StringUtils.isEmpty(messageDto.getMessageDtoRequest())) {
            MessageDtoRequest messageDtoRequest = (MessageDtoRequest) JsonUtils.fromJson(messageDto.getMessageDtoRequest(), new TypeToken<MessageDtoRequest>(){}.getType());
            if ("restart".equals(messageDtoRequest.getOperation()) && !StringUtils.isEmpty(messageDtoRequest.getUserCode())) {
                restartMining(messageDtoRequest.getUserCode());
            }

            if ("stop".equals(messageDtoRequest.getOperation()) && !StringUtils.isEmpty(messageDtoRequest.getUserCode())) {
                stopMining(messageDtoRequest.getUserCode());
            }
        }else {
            SocketResponse socketResponse = (SocketResponse) JsonUtils.fromJson(messageDto.getSocketResponse(), new TypeToken<SocketResponse>(){}.getType());
            session.sendText(JsonUtils.toJson(socketResponse));
        }
    }

    /**
     * 启动挖矿
     * @param session
     * @param userCode
     */
    private void startMining(Session session, String userCode) {
        // 校验是否已经授权
        Boolean isAuth = MiningConstant.authStatusMap.get(userCode);
        if (null == isAuth || !isAuth) {
            toMessageAndSend(ResultEnum.SK_NOT_AUTH.getCode(), ResultEnum.SK_NOT_AUTH.getMsg(), null, session);
            return;
        }
        toMessageAndSend(ResultEnum.SK_IS_AUTH.getCode(), ResultEnum.SK_IS_AUTH.getMsg(), null, session);
        // 将userCode移除断开队列
        MiningConstant.sessionMap.put(userCode, session);
        MiningConstant.userCodeMap.put(session.id().asShortText(), userCode);
        // 给用户创建一个线程
        ExecutorService executorService = miningService.createExecutorService(userCode);
        Boolean isRun = MiningConstant.executorStatusMap.get(executorService);
        if (isRun) {
            return;
        }
        // 启动挖矿
        miningService.startMining(userCode, executorService);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("SMGAME日志消息 --> 用户：{" + userCode + "} 于{" + format.format(new Date()) + "} 启动挖矿。");
        MiningConstant.executorStatusMap.put(executorService, true);
    }

    /**
     * 重新启动挖矿
     * @param userCode
     */
    private void restartMining(String userCode) {
        stopMining(userCode);

        ExecutorService executorService = miningService.createExecutorService(userCode);

        // 启动挖矿
        miningService.startMining(userCode, executorService);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("SMGAME日志消息 --> 用户：{" + userCode + "} 于{" + format.format(new Date()) + "} 重新启动挖矿。");
        MiningConstant.executorStatusMap.put(executorService, true);
    }

    /**
     * 停止挖矿
     * @param userCode
     */
    private void stopMining(String userCode) {
        ExecutorService executorService = MiningConstant.executorMap.get(userCode);
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        }catch (Exception e) {
            logger.error("SMGAME日志消息 --> 用户：{" + userCode + "} 停止挖矿时出现异常。", e);
            executorService.shutdownNow();
        }

        initData(userCode);
    }

    /**
     * 初始化数据
     * @param userCode
     */
    public void initData(String userCode) {
        // 移除session
        Session session = MiningConstant.sessionMap.get(userCode);
        MiningConstant.userCodeMap.remove(session.id().asShortText());
        MiningConstant.sessionMap.remove(userCode);
    }

    /**
     * 切换矿区
     * @param userCode
     */
    public void changeArea(String userCode) {
        ExecutorService executorService = MiningConstant.executorMap.get(userCode);
        if (executorService.isShutdown()) {
            startMining(MiningConstant.sessionMap.get(userCode), userCode);
        }
    }

    private UserProfileResponse toUserProfileResponse(String userCode) {
        UserProfile userProfile = userProfileService.getUserProfileByUserCode(userCode);
        UserWallet userWallet = userWalletService.getUserWalletByUserCode(userCode);
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserCode(userCode);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userProfileResponse.setCreateTime(format.format(userProfile.getCreateTime()));
        userProfileResponse.setCommonCoin(userWallet.getCommonCoin());
        userProfileResponse.setSeniorCoin(userWallet.getSeniorCoin());
        userProfileResponse.setRareCoin(userWallet.getRareCoin());
        userProfileResponse.setFewCoin(userWallet.getFewCoin());
        return userProfileResponse;
    }

    public void toMessageAndSend(Integer code, String msg, String data, Session session) {
        SocketResponse socketResponse = new SocketResponse();
        socketResponse.setCode(code);
        socketResponse.setMsg(msg);
        socketResponse.setData(data);
        MessageDto messageDto = new MessageDto();
        messageDto.setSocketResponse(JsonUtils.toJson(socketResponse));
        OnMessage(session, JsonUtils.toJson(messageDto));
    }

    /*@OnEvent
    public void onEvent(Session session, Object evt) {
        System.out.println(session.id().asShortText());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }*/
}