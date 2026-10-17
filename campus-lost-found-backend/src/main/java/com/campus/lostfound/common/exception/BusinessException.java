package com.campus.lostfound.common.exception;

import com.campus.lostfound.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于在业务逻辑中抛出可预期的错误，由全局异常处理器统一捕获
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 快捷工厂方法
     */
    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(ResultCode.UNAUTHORIZED, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ResultCode.FORBIDDEN, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ResultCode.NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ResultCode.CONFLICT, message);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(ResultCode.BAD_REQUEST, message);
    }
}
