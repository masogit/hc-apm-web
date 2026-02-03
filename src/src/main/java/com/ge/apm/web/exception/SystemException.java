package com.ge.apm.web.exception;

public class SystemException extends BusinessException {
    public SystemException(String message, Throwable cause) {
        super("500", message, cause);
    }

    public SystemException(Throwable cause) {
        super("500", "系统内部错误", cause);
    }
}
