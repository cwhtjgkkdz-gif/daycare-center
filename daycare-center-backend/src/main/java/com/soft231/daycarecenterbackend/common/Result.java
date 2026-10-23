package com.soft231.daycarecenterbackend.common;

import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result() {}

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS;
        r.message = "success";
        return r;
    }

    /** 返回成功消息（无数据，用于增删改等操作） */
    public static Result<Void> ok(String message) {
        Result<Void> r = new Result<>();
        r.code = ResultCode.SUCCESS;
        r.message = message;
        return r;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> ok(String message, T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.code = ResultCode.ERROR;
        r.message = message;
        return r;
    }
}
