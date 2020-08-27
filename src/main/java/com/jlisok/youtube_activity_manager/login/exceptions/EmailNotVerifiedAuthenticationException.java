package com.jlisok.youtube_activity_manager.login.exceptions;

import org.springframework.security.core.AuthenticationException;

public class EmailNotVerifiedAuthenticationException extends AuthenticationException {

    public EmailNotVerifiedAuthenticationException(String msg) {
        super(msg);
    }
}
