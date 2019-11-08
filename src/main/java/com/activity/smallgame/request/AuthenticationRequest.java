package com.activity.smallgame.request;

import lombok.Data;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 18:03
 **/
@Data
public class AuthenticationRequest {

    private String mobile;

    private String userCode;
}
