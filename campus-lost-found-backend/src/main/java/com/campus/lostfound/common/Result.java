package com.campus.lostfound.common;

import lombok.Data;

/**
 * 统一响应格式
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /** 状态码 */
    private int code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    /** 表单校验错误详情 */
    private Object errors;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（仅消息）
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success("操作成功");
    }

    /**
     * 快捷别名：ok(data)
     */
    public static <T> Result<T> ok(T data) {
        return success(data);
    }

    /**
     * 快捷别名：ok(message, data)
     */
    public static <T> Result<T> ok(String message, T data) {
        return success(message, data);
    }

    /**
     * 快捷别名：ok(message)
     */
    public static <T> Result<Void> ok(String message) {
        return success(message);
    }

    /**
     * 快捷别名：ok()
     */
    public static <T> Result<T> ok() {
        return success();
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（带错误详情）
     */
    public static <T> Result<T> error(ResultCode resultCode, String message, Object errors) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        result.setErrors(errors);
        return result;
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
