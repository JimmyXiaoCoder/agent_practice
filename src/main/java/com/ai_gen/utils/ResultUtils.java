package com.ai_gen.utils;

import com.ai_gen.enums.ErrorCode;
import com.ai_gen.response.BaseResponse;

public class ResultUtils {

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(null,200,"操作成功");
    }

    public static <T> BaseResponse<T>  success(T data) {
        return new BaseResponse(data,200);
    }

    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(null,errorCode.getCode(),errorCode.getMessage());
    }

    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(null,errorCode.getCode(), message);
    }

    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(null,code, message);
    }

}
