package com.jlisok.youtube_activity_manager.login.exceptions;

import org.springframework.security.core.AuthenticationException;

public class DataInconsistencyAuthenticationException extends AuthenticationException {

    public DataInconsistencyAuthenticationException(String detail) {
        super(detail);
    }
}
