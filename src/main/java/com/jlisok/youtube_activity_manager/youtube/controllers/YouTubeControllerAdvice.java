package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.jlisok.youtube_activity_manager.domain.exceptions.BaseExceptionHandler;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class YouTubeControllerAdvice extends BaseExceptionHandler {


    @Override
    protected Logger getLoggerInstance() {
        return LoggerFactory.getLogger(this.getClass());
    }

    @Override
    protected String getLoggerMessage() {
        return "{} Retrieving data from YouTube Api failed.";
    }

    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Object> handleFailedLoginException(AuthenticationCredentialsNotFoundException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.YOUTUBE_API_REQUEST_FAILED_ACCESS_TOKEN_FAILURE, HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler({GoogleJsonResponseException.class})
    public ResponseEntity<Object> handleFailedLoginException(GoogleJsonResponseException exception) {
        return handleExceptionWithErrorLogging(ResponseCode.YOUTUBE_API_REQUEST_FAILED_INVALID_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }
}
