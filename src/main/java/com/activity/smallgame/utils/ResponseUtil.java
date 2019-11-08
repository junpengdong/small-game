package com.activity.smallgame.utils;

import com.activity.smallgame.dto.ResultDto;
import com.activity.smallgame.enums.ResultEnum;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 16:55
 **/
public class ResponseUtil {

    public static ResultDto success(Object object){
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(ResultEnum.SUCCESS.getCode());
        resultDto.setMessage(ResultEnum.SUCCESS.getMsg());
        resultDto.setData(object);
        return resultDto;
    }

    public static ResultDto success(){
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(ResultEnum.SUCCESS.getCode());
        resultDto.setMessage(ResultEnum.SUCCESS.getMsg());
        resultDto.setData(null);
        return resultDto;
    }

    public static ResultDto error(Integer code, String msg){
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(code);
        resultDto.setMessage(msg);
        return resultDto;
    }
}
