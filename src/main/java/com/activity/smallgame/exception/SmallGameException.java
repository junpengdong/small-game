package com.activity.smallgame.exception;

import com.activity.smallgame.enums.ResultEnum;
import lombok.Getter;

@Getter
public class SmallGameException extends RuntimeException {

    private Integer code;

    public SmallGameException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());

        this.code = resultEnum.getCode();
    }

    public SmallGameException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
