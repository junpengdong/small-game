package com.activity.smallgame.service;

import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: Mr.dong
 * @create: 2019-11-08 16:22
 **/
@Service
public class LuckDrawService {

    // 基数列表
    private static List<Double> baseList = new CopyOnWriteArrayList<>();

    // 倍数列表
    private static List<Double> multipleList = new CopyOnWriteArrayList<>();

    // 扣费列表
    private static List<Double> resetList = new CopyOnWriteArrayList<>();

    @Autowired
    private StringRedisTemplate redis;

    static {
        baseList.add(5.2);
        baseList.add(13.14);
        baseList.add(52.0);
        baseList.add(131.4);
        baseList.add(520.0);
        baseList.add(1314.0);

        multipleList.add(1.0);
        multipleList.add(1.1);
        multipleList.add(1.2);
        multipleList.add(1.3);
        multipleList.add(1.4);
        multipleList.add(1.5);

        resetList.add(5.2);
        resetList.add(13.14);
        resetList.add(52.0);
        resetList.add(131.4);
        resetList.add(520.0);
    }

    public Double openBase(String mobile) {
        if (!checkMobile(mobile)) {
            throw new SmallGameException(ResultEnum.ERROR_PARAMS);
        }
        // 判断是否可以抽奖
        String value = redis.opsForValue().get("cache.draw." + mobile);
        if (!StringUtils.isEmpty(value)) {
            throw new SmallGameException(ResultEnum.LUCK_DRAW_ALREADY);
        }
        Collections.shuffle(baseList);
        Double base = baseList.get(0);
        redis.opsForValue().set("cache.base." + mobile, String.valueOf(base));
        redis.opsForValue().set("cache.draw." + mobile, "1");
        return base;
    }

    public Double openMultiple(String mobile) {
        if (!checkMobile(mobile)) {
            throw new SmallGameException(ResultEnum.ERROR_PARAMS);
        }
        // 判断是否抽过奖
        String value = redis.opsForValue().get("cache.draw." + mobile);
        if (StringUtils.isEmpty(value)) {
            throw new SmallGameException(ResultEnum.NOT_LUCK_DRAW);
        }
        // 判断是否已经翻倍过
        value = redis.opsForValue().get("cache.,multiple." + mobile);
        if (!StringUtils.isEmpty(value)) {
            throw new SmallGameException(ResultEnum.LUCK_DRAW_MULTIPLE_ALREADY);
        }
        Collections.shuffle(multipleList);
        Double multiple = multipleList.get(0);
        Double base = Double.valueOf(value);
        redis.opsForValue().set("cache.base." + mobile, String.valueOf(base * multiple));
        redis.opsForValue().set("cache.,multiple." + mobile, "1");
        return multiple;
    }

    public Double resetOpenBase(String mobile) {
        if (!checkMobile(mobile)) {
            throw new SmallGameException(ResultEnum.ERROR_PARAMS);
        }
        // 判断是否抽过奖
        String value = redis.opsForValue().get("cache.draw." + mobile);
        if (StringUtils.isEmpty(value)) {
            throw new SmallGameException(ResultEnum.NOT_LUCK_DRAW);
        }
        // 判断是否已经重置过
        value = redis.opsForValue().get("cache.reset." + mobile);
        if (!StringUtils.isEmpty(value)) {
            throw new SmallGameException(ResultEnum.LUCK_DRAW_RESET_ALREADY);
        }
        value = redis.opsForValue().get("cache.base." + mobile);
        if (StringUtils.isEmpty(value)) {
            redis.delete("cache.draw." + mobile);
            throw new SmallGameException(ResultEnum.NOT_LUCK_DRAW);
        }
        Double base = Double.valueOf(value);
        Collections.shuffle(resetList);
        Double reset = resetList.get(0);
        redis.opsForValue().set("cache.base." + mobile, String.valueOf(base - reset));
        redis.opsForValue().set("cache.reset." + mobile, "1");
        return reset;
    }

    private boolean checkMobile(String mobile) {
        return mobile.equals("13824797146");
    }
}
