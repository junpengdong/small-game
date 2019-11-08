package com.activity.smallgame.response;

import lombok.Data;

/**
 * @author: Mr.dong
 * @create: 2019-08-23 14:55
 **/
@Data
public class SocketResponse {

    private int code;

    private String msg;

    private Object data;
}
