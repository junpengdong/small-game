package com.activity.smallgame.service;

import com.activity.smallgame.model.mongodb.MiningRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: Mr.dong
 * @create: 2019-08-23 11:54
 **/
@Service
public class MiningRecordService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加挖矿记录
     * @param userCode
     * @param coin
     * @param result
     */
    public void addMiningRecord(String userCode, Double coin, int result) {
        String coinName;
        switch (result) {
            case 4:
                coinName = "普通矿币";
                break;
            case 5:
                coinName = "高级矿币";
                break;
            case 6:
                coinName = "稀有矿币";
                break;
            default:
                coinName = "罕见矿币";
                break;
        }
        MiningRecord miningRecord = new MiningRecord(userCode, coin, result, coinName, new Date());
        mongoTemplate.save(miningRecord);
    }
}
