package com.activity.smallgame.constant;

import com.activity.smallgame.model.mongodb.MiningArea;
import com.activity.smallgame.model.mongodb.UserProfile;
import org.yeauty.pojo.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 20:09
 **/
public interface MiningConstant {

    String CACHE_USER_WALLET = "cache.user.wallet.%s";

    String CACHE_DISCONNECT_USER = "cache.disconnect.user";

    String CACHE_SEND_MAIL = "cache.send.mail.%s.%s";

    String CACHE_RESULT_FIVE_COUNT = "cache.result.five.count.%s";

    Map<String, Boolean> authStatusMap = new ConcurrentHashMap<>();

    Map<String, UserProfile> userProfileMap = new ConcurrentHashMap<>();

    Map<String, ExecutorService> executorMap = new ConcurrentHashMap<>();

    Map<ExecutorService, Boolean> executorStatusMap = new ConcurrentHashMap<>();

    Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    Map<String, String> userCodeMap = new ConcurrentHashMap<>();

    Map<String, MiningArea> miningAreaMap = new ConcurrentHashMap<>();
}
