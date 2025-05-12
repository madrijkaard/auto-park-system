package com.rkd.auto.type;

public enum ExceptionType {

    INVALID_INPUT_FIELD("INVALID_INPUT", "Invalid input field"),
    NOT_FOUND("NOT_FOUND", "%s not found");

    private final String code;
    private final String message;

    ExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String formatMessage(Object... args) {
        return (args != null && args.length > 0) ? String.format(message, args) : message;
    }
}