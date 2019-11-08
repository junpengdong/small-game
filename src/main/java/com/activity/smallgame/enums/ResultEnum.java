package com.activity.smallgame.enums;

import lombok.Getter;

/**
 * @author: Mr.dong
 * @create: 2019-07-09 17:43
 **/
@Getter
public enum  ResultEnum {

    SUCCESS(1, "SUCCESS"),
    FAILED(2, "FAILED"),
    ERROR_PARAMS(3, "ERROR PARAMS"),
    INNER_ERROR(4, "INNER ERROR"),

    MOBILE_EXIST(1001, "Mobile exist"),
    USER_PROFILE_NOT_EXIST(1002, "User profile not exist"),
    USER_WALLET_EXIST(1003, "User wallet is exist"),
    USER_WALLET_NOT_EXIST(1004, "User wallet not exist"),
    MINING_AREA_SURPLUS_ZERO(1005, "Mining area surplus zero"),
    SEND_MAIL_ALREADY_TODAY(1006, "Send Mail Already to day"),

    // socket code
    SK_NOT_AUTH(2000, "NOT AUTH"),
    SK_IS_AUTH(2001, "IS AUTH"),

    SK_USER_PROFILE(3000, "USER PROFILE");

    private int code;

    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
