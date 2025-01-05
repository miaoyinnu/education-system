package com.education.common;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final int code;
    private final String message;

    public BaseException(String message) {
        this(ResultCode.ERROR, message);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
} 