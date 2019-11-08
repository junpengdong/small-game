package com.activity.smallgame.model.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户钱包
 * @author: Mr.dong
 * @create: 2019-08-20 17:31
 **/
@Data
@Document(collection = "user_wallet")
public class UserWallet {

    @Indexed(unique = true)
    private String userCode;

    /**
     * 普通矿币
     */
    private double commonCoin;

    /**
     * 高级矿币
     */
    private double seniorCoin;

    /**
     * 稀有矿币
     */
    private double rareCoin;

    /**
     * 罕见矿币
     */
    private double fewCoin;
}
