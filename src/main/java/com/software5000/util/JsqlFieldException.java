package com.software5000.util;

public class JsqlFieldException extends RuntimeException {
    public JsqlFieldException() {
        super();
    }

    public JsqlFieldException(String message) {
        super(message);
    }

    public JsqlFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsqlFieldException(Throwable cause) {
        super(cause);
    }

    protected JsqlFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
