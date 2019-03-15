package com.software5000.util;

public class BpMybatisException extends RuntimeException {
    public BpMybatisException() {
        super();
    }

    public BpMybatisException(String message) {
        super(message);
    }

    public BpMybatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpMybatisException(String message, Throwable cause, String... messageParam) {
        super(String.format(message, messageParam), cause);
    }

    public BpMybatisException(Throwable cause) {
        super(cause);
    }

    protected BpMybatisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
