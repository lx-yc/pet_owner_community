package com.example.exception;

import com.example.enums.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(int code,String message) {
        super(message);
        this.code = code;
    }

//    public int getCode() {
//        return code;
//    }
}
