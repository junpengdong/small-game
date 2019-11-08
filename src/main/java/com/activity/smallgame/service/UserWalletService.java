package com.activity.smallgame.service;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import com.activity.smallgame.model.mongodb.UserWallet;
import com.activity.smallgame.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author: Mr.dong
 * @create: 2019-08-22 10:29
 **/
@Service
public class UserWalletService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate redis;

    public void addUserWallet(String userCode) {
        Query query = new Query(Criteria.where("userCode").is(userCode));
        UserWallet userWallet = mongoTemplate.findOne(query, UserWallet.class);
        if (userWallet != null) {
            throw new SmallGameException(ResultEnum.USER_WALLET_EXIST);
        }
        userWallet = new UserWallet();
        userWallet.setUserCode(userCode);
        mongoTemplate.save(userWallet);
    }

    public UserWallet getUserWalletByUserCode(String userCode) {
        String cacheKey = String.format(MiningConstant.CACHE_USER_WALLET, userCode);
        String cacheValue = redis.opsForValue().get(cacheKey);
        if (!StringUtils.isEmpty(cacheValue)) {
            return (UserWallet) JsonUtils.fromJson(cacheValue, new TypeToken<UserWallet>(){}.getType());
        }
        Query query = new Query(Criteria.where("userCode").is(userCode));
        UserWallet userWallet = mongoTemplate.findOne(query, UserWallet.class);
        if (null == userWallet) {
            throw new SmallGameException(ResultEnum.USER_WALLET_NOT_EXIST);
        }
        redis.opsForValue().set(cacheKey, JsonUtils.toJson(userWallet));
        return userWallet;
    }

    public void updateUserWallet(String userCode, UserWallet userWallet) {
        String cacheKey = String.format(MiningConstant.CACHE_USER_WALLET, userCode);
        mongoTemplate.updateFirst(new Query(Criteria.where("userCode").is(userCode)), new Update().set("commonCoin", userWallet.getCommonCoin())
                .set("seniorCoin", userWallet.getSeniorCoin()).set("rareCoin", userWallet.getRareCoin()).set("fewCoin", userWallet.getFewCoin()), UserWallet.class);
        redis.opsForValue().set(cacheKey, JsonUtils.toJson(userWallet));
    }
}
