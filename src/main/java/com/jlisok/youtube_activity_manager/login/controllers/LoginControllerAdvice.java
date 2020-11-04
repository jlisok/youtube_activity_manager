package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.BaseExceptionHandler;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.login.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.FailedLoginException;

@ControllerAdvice
public class LoginControllerAdvice extends BaseExceptionHandler {

    @Override
    protected Logger getLoggerInstance() {
        return LoggerFactory.getLogger(this.getClass());
    }

    @Override
    protected String getLoggerMessage() {
        return "{} Login failed.";
    }

    @ExceptionHandler({FailedLoginException.class})
    public ResponseEntity<Object> handleFailedLoginException(FailedLoginException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE, HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Object> handleFailedLoginException(AuthenticationCredentialsNotFoundException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.LOGIN_FAILED_GOOGLE_TOKEN_INVALID, HttpStatus.BAD_REQUEST, exception);
    }


    @ExceptionHandler({EmailNotVerifiedAuthenticationException.class})
    public ResponseEntity<Object> handleFailedLoginException(EmailNotVerifiedAuthenticationException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.LOGIN_FAILED_EMAIL_NOT_VERIFIED, HttpStatus.FORBIDDEN, exception);
    }


    @ExceptionHandler({DataInconsistencyAuthenticationException.class})
    public ResponseEntity<Object> handleFailedLoginException(DataInconsistencyAuthenticationException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.LOGIN_FAILED_GOOGLE_ACCOUNT_ALREADY_EXISTS, HttpStatus.FORBIDDEN, exception);
    }

    @ExceptionHandler({GoogleIdsDoNotMatchException.class})
    public ResponseEntity<Object> handleFailedLoginException(GoogleIdsDoNotMatchException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.AUTHORIZATION_FAILED_GOOGLE_ID_INCONSISTENT_WITH_DATABASE, HttpStatus.FORBIDDEN, exception);
    }

    @ExceptionHandler({AuthorizationException.class})
    public ResponseEntity<Object> handleFailedLoginException(AuthorizationException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.AUTHORIZATION_FAILED_USER_NOT_FOUND, HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler({DemoUserNotFound.class})
    public ResponseEntity<Object> handleFailedLoginException(DemoUserNotFound exception) {
        return handleExceptionWithInfoLogging(ResponseCode.AUTHORIZATION_FAILED_USER_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

}
