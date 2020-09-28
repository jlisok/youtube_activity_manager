package com.jlisok.youtube_activity_manager.domain.exceptions;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseExceptionHandler {

    public ResponseEntity<Object> handleExceptionWithInfoLogging(ResponseCode responseCode, HttpStatus status, Throwable throwable) {
        Logger logger = getLoggerInstance();
        String message = getLoggerMessage();
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(responseCode);
        logger.info(message, customErrorResponse.getId(), throwable);
        return ResponseEntity
                .status(status)
                .body(customErrorResponse);
    }

    public ResponseEntity<Object> handleExceptionWithErrorLogging(ResponseCode responseCode, HttpStatus status, Throwable throwable) {
        Logger logger = getLoggerInstance();
        String message = getLoggerMessage();
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(responseCode);
        logger.error(message, customErrorResponse.getId(), throwable);
        return ResponseEntity
                .status(status)
                .body(customErrorResponse);
    }

    protected abstract Logger getLoggerInstance();

    protected abstract String getLoggerMessage();
}
