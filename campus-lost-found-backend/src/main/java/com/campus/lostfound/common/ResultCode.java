package com.campus.lostfound.common;

import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 参数校验失败 */
    BAD_REQUEST(400, "参数校验失败"),

    /** 未认证 */
    UNAUTHORIZED(401, "请先登录"),

    /** 无权限 */
    FORBIDDEN(403, "无权限访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 业务冲突 */
    CONFLICT(409, "操作冲突"),

    /** 请求过于频繁或账号锁定 */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),

    /** 服务器内部错误 */
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
