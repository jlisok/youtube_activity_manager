package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
}
