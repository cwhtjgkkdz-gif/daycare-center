package com.soft231.daycarecenterbackend.common;

/**
 * 返回状态码
 */
public interface ResultCode {

    int SUCCESS = 200;
    int ERROR = 500;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
}
