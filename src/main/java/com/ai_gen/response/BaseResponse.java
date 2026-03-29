package com.ai_gen.response;

import com.ai_gen.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private T data;

    private int code;

    private String message;

    public BaseResponse(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public BaseResponse(int code, String message) {
        this(null,code,message);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(null,errorCode.getCode(),errorCode.getMessage());
    }

    public BaseResponse(T data,int code) {
        this(data,code,"");
    }
}
