package com.activity.smallgame.response;

import lombok.Data;

/**
 * @author: Mr.dong
 * @create: 2019-08-30 14:50
 **/
@Data
public class UserProfileResponse {

    private String userCode;

    private String createTime;

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
