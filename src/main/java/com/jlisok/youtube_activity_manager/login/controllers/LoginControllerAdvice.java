package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.FailedLoginException;

@ControllerAdvice
public class LoginControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String loggerMessage = "{} Login failed.";

    @ExceptionHandler({FailedLoginException.class})
    public ResponseEntity<Object> handleFailedLoginException(FailedLoginException failedLoginException) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE);
        logger.info(loggerMessage, customErrorResponse.getId(), failedLoginException);
        return ResponseEntity
                .badRequest()
                .body(customErrorResponse);
    }


    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Object> handleFailedLoginException(AuthenticationCredentialsNotFoundException authenticationCredentialsNotFoundException) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.LOGIN_FAILED_GOOGLE_TOKEN_INVALID);
        logger.info(loggerMessage, customErrorResponse.getId(), authenticationCredentialsNotFoundException);
        return ResponseEntity
                .badRequest()
                .body(customErrorResponse);
    }

    @ExceptionHandler({EmailNotVerifiedAuthenticationException.class})
    public ResponseEntity<Object> handleFailedLoginException(EmailNotVerifiedAuthenticationException exception) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.LOGIN_FAILED_EMAIL_NOT_VERIFIED);
        logger.info(loggerMessage, customErrorResponse.getId(), exception);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(customErrorResponse);
    }

    @ExceptionHandler({DataInconsistencyAuthenticationException.class})
    public ResponseEntity<Object> handleFailedLoginException(DataInconsistencyAuthenticationException exception) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.LOGIN_FAILED_GOOGLE_ACCOUNT_ALREADY_EXISTS);
        logger.info(loggerMessage, customErrorResponse.getId(), exception);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(customErrorResponse);
    }

}
