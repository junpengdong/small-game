package com.activity.smallgame.schedule;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.socket.NettyWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author: Mr.dong
 * @create: 2019-08-22 11:26
 **/
@Service
public class DisconnectSchedule {

    private Logger logger = LoggerFactory.getLogger(DisconnectSchedule.class);

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private NettyWebSocket nettyWebSocket;

    @Scheduled(cron = "0/30 * * * * ?")
    public void checkQueueList() {
        Map<Object, Object> discUserMap = redis.opsForHash().entries(MiningConstant.CACHE_DISCONNECT_USER);
        List<String> removeUserCodeList = new ArrayList<>();

        for (Map.Entry entry : discUserMap.entrySet()) {
            String key = (String) entry.getKey();
            Integer seconds = Integer.valueOf((String) entry.getValue());
            if (seconds >= 5 * 60) {
                removeUserCodeList.add(key);
            }else {
                redis.opsForHash().put(MiningConstant.CACHE_DISCONNECT_USER, key, String.valueOf(seconds + 30));
            }
        }

        removeUserCodeList.forEach(userCode -> {
            redis.opsForHash().delete(MiningConstant.CACHE_DISCONNECT_USER, userCode);
            // 移除登陆状态
            MiningConstant.authStatusMap.remove(userCode);
            nettyWebSocket.initData(userCode);

            // 移除线程状态
            ExecutorService executorService = MiningConstant.executorMap.get(userCode);
            MiningConstant.executorStatusMap.remove(executorService);

            // 移除线程
            MiningConstant.executorMap.remove(userCode);
        });
    }
}
