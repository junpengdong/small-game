package com.activity.smallgame.dto;

import lombok.Data;

/**
 * @author: Mr.dong
 * @create: 2019-07-15 20:14
 **/
@Data
public class ResultDto<T> {

    private int code;

    private String message;

    private T data;
}
