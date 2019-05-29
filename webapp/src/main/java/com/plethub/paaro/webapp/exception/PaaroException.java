package com.plethub.paaro.webapp.exception;

public class PaaroException extends RuntimeException {
    public PaaroException(String msg, Throwable t) {
        super(msg, t);
    }

    public PaaroException(String msg) {
        super(msg);
    }
}
