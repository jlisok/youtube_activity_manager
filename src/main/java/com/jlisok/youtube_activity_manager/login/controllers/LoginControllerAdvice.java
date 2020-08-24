package com.jlisok.youtube_activity_manager.login.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.FailedLoginException;

@ControllerAdvice
public class LoginControllerAdvice {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler({FailedLoginException.class})
    public ResponseEntity<Object> handleFailedLoginException(FailedLoginException failedLoginException) {
        logger.info(failedLoginException.getMessage(), failedLoginException.getCause());
        return ResponseEntity
                .badRequest()
                .body(failedLoginException.getMessage());
    }
}
