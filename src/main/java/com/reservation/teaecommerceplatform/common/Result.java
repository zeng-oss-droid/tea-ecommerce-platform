package com.reservation.teaecommerceplatform.common;

import lombok.Data;

/**
 * 前后端约定的统一 JSON 结构：{@code code}（200 成功、500 业务错误等）、{@code message}、{@code data}。
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    /** 成功且仅返回 data，提示语为默认「操作成功」 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /** 成功并自定义 message */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /** 业务失败，默认 code=500 */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /** 自定义错误码（如 401 未登录） */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

