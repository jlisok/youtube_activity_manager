package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationDataProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RegistrationControllerAdvice {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException.class})
    public ResponseEntity<Object> handlePrefixAndPhoneNumberMustBeBothEitherNullOrFilledException(PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException e) {
        logger.info(e.getExceptionMessage().toString());
        return ResponseEntity
                .badRequest()
                .body(e.getExceptionMessage());
    }


    @ExceptionHandler({BadRegistrationRequestException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(BadRegistrationRequestException e) {
        if (e.getThrowable().getMessage() != null) {
            logger.info(e.getExceptionMessage().getMessage(), e.getThrowable());
        } else {
            logger.info(e.getExceptionMessage().toString());
        }
        return ResponseEntity
                .badRequest()
                .body(e.getExceptionMessage());
    }

    @ExceptionHandler({RegistrationDataProcessingException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(RegistrationDataProcessingException e) {
        if (e.getThrowable().getMessage() != null) {
            logger.info(e.getExceptionMessage().getMessage(), e.getThrowable());
        } else {
            logger.info(e.getExceptionMessage().toString());
        }
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(e.getExceptionMessage());
    }

}
