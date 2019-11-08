package com.activity.smallgame.model.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author: Mr.dong
 * @create: 2019-08-23 11:48
 **/
@Data
@Document(collection = "mining_record")
@CompoundIndexes({
        @CompoundIndex(name = "idx_userCode_coinName", def = "{'userCode': 1, 'coinName': 1}")
})
public class MiningRecord {

    private String userCode;

    private Double addCoin;

    private int result;

    private String coinName;

    private Date createTime;

    public MiningRecord() {
    }

    public MiningRecord(String userCode, Double addCoin, int result, String coinName, Date createTime) {
        this.userCode = userCode;
        this.addCoin = addCoin;
        this.result = result;
        this.coinName = coinName;
        this.createTime = createTime;
    }
}
