package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException.class})
    public ResponseEntity<Object> handlePrefixAndPhoneNumberMustBeBothEitherNullOrFilledException(PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_PREFIX_PHONE_NUMBER_MUST_BE_FILLED_IN_OR_NULL);
        logger.info(e.getMessage(), customErrorResponse, e);
        return ResponseEntity
                .status(customErrorResponse.getHttpStatus())
                .body(customErrorResponse);
    }


    @ExceptionHandler({BadRegistrationRequestException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(BadRegistrationRequestException e) {
        if (e.getCustomErrorResponse().getHttpStatus().is5xxServerError()) {
            logger.error(e.getMessage(), e);
        } else {
            logger.info(e.getMessage(), e);
        }
        return ResponseEntity
                .status(e.getCustomErrorResponse().getHttpStatus())
                .body(e.getCustomErrorResponse());

    }


    @ExceptionHandler({RegistrationDataProcessingException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(RegistrationDataProcessingException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_SOME_PARAMETERS_NULL, INTERNAL_SERVER_ERROR);
        logger.info(e.getMessage(), customErrorResponse, e);
        return ResponseEntity
                .status(customErrorResponse.getHttpStatus())
                .body(customErrorResponse);
    }

}
