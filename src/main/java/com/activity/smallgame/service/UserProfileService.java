package com.activity.smallgame.service;

import com.activity.smallgame.constant.MailConstant;
import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import com.activity.smallgame.model.mongodb.MiningArea;
import com.activity.smallgame.model.mongodb.UserProfile;
import com.activity.smallgame.socket.NettyWebSocket;
import com.activity.smallgame.utils.IdGenerator;
import com.activity.smallgame.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: Mr.dong
 * @create: 2019-08-20 17:45
 **/
@Service
public class UserProfileService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private NettyWebSocket nettyWebSocket;

    private static final String CACHE_USER_PROFILE = "cache.user.profile.%s";

    private static final String CACHE_HELP_DATE = "cache.help.date.%s.%s";

    private static final String AUTH_CODE = "5fe8712e-ae36-4cbb-ac8e-7fae137f7f02";

    public void addUserProfile(String name, String mobile, String email, String authCode) {
        if (!AUTH_CODE.equals(authCode)) {
            return;
        }
        Query query = new Query(Criteria.where("mobile").is(mobile));
        UserProfile userProfile = mongoTemplate.findOne(query, UserProfile.class);
        if (userProfile != null) {
            throw new SmallGameException(ResultEnum.MOBILE_EXIST);
        }
        List<MiningArea> miningAreas = mongoTemplate.find(new Query(), MiningArea.class);
        Long areaCode = null;
        if (!CollectionUtils.isEmpty(miningAreas)) {
            areaCode = miningAreas.get(0).getAreaCode();
        }
        String userCode = String.valueOf(IdGenerator.getInstance().nextId());
        userProfile = new UserProfile(userCode, name, mobile, email, new Date(), areaCode);
        mongoTemplate.save(userProfile);

        userWalletService.addUserWallet(userCode);

        String content = String.format(MailConstant.MAIL_CONTENT_TO, userCode);
        sendMailService.sendSimpleMail(email, MailConstant.MAIL_SUBJECT_TO, content, null, true);
    }

    public UserProfile getUserProfileByMobileAndUserCode(String mobile, String userCode) {
        Query query = new Query(Criteria.where("mobile").is(mobile).and("userCode").is(userCode));
        UserProfile userProfile = mongoTemplate.findOne(query, UserProfile.class);
        if (null == userProfile) {
            return null;
        }
        MiningConstant.authStatusMap.put(userCode, true);
        return userProfile;
    }


    public UserProfile getUserProfileByUserCode(String userCode) {
        String cacheKey = String.format(CACHE_USER_PROFILE, userCode);
        String cacheValue = redis.opsForValue().get(cacheKey);
        UserProfile userProfile;
        if (!StringUtils.isEmpty(cacheValue)) {
            return (UserProfile) JsonUtils.fromJson(cacheValue, new TypeToken<UserProfile>(){}.getType());
        }
        Query query = new Query(Criteria.where("userCode").is(userCode));
        userProfile = mongoTemplate.findOne(query, UserProfile.class);
        if (null == userProfile) {
            return null;
        }
        redis.opsForValue().set(cacheKey, JsonUtils.toJson(userProfile));
        return userProfile;
    }

    public void addMultiple(String userCode) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = simpleDateFormat.format(new Date());
        String cacheKey = String.format(CACHE_HELP_DATE, userCode, nowDate);
        String cacheValue = redis.opsForValue().get(cacheKey);
        if (!StringUtils.isEmpty(cacheValue)) {
            return;
        }
        UserProfile userProfile = getUserProfileByUserCode(userCode);
        if (null == userProfile) {
            throw new SmallGameException(ResultEnum.USER_PROFILE_NOT_EXIST);
        }
        if (userProfile.getMultiple() >= 1.5) {
            return;
        }
        double multiple = userProfile.getMultiple() + 0.1;
        mongoTemplate.updateFirst(new Query(Criteria.where("userCode").is(userCode)), new Update().set("multiple", multiple), UserProfile.class);
        redis.opsForValue().set(cacheKey, "1");

        // 更新全局变量userProfile
        MiningConstant.userProfileMap.put(userCode, userProfile);
    }

    public void updateUserProfile(UserProfile userProfile) {
        String cacheKey = String.format(CACHE_USER_PROFILE, userProfile.getUserCode());
        mongoTemplate.updateFirst(new Query(Criteria.where("userCode").is(userProfile.getUserCode())), new Update().set("areaCode", userProfile.getUserCode()), UserProfile.class);
        redis.delete(cacheKey);
    }
}
