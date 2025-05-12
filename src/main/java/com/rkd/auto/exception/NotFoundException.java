package com.rkd.auto.exception;

import com.rkd.auto.type.ExceptionType;

public class NotFoundException extends RuntimeException {

    private final String code;
    private final String description;

    public NotFoundException(ExceptionType exceptionType, Object... args) {
        super(exceptionType.formatMessage(args));
        this.code = exceptionType.code();
        this.description = exceptionType.formatMessage(args);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

