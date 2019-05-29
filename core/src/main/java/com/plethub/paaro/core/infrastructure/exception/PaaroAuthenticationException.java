package com.plethub.paaro.core.infrastructure.exception;

import org.springframework.security.core.AuthenticationException;

public class PaaroAuthenticationException extends AuthenticationException {
    public PaaroAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public PaaroAuthenticationException(String msg) {
        super(msg);
    }
}
