package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.registration.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@ControllerAdvice
public class RegistrationControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String loggerMessage = "{} Registration failed.";

    @ExceptionHandler({PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException.class})
    public ResponseEntity<Object> handlePrefixAndPhoneNumberMustBeBothEitherNullOrFilledException(PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_PREFIX_PHONE_NUMBER_MUST_BE_FILLED_IN_OR_NULL);
        logger.info(loggerMessage, customErrorResponse.getId(), e);
        return ResponseEntity
                .badRequest()
                .body(customErrorResponse);
    }


    @ExceptionHandler({FieldViolationBadRegistrationRequestException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(BadRegistrationRequestException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_VIOLATED_FIELD_EMAIL);
        logger.info(loggerMessage, customErrorResponse.getId(), e);
        return ResponseEntity
                .badRequest()
                .body(customErrorResponse);
    }


    @ExceptionHandler({UnexpectedErrorBadRegistrationRequestException.class})
    public ResponseEntity<Object> handleUnexpectedErrorRegistrationRequestException(UnexpectedErrorBadRegistrationRequestException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR);
        logger.error(loggerMessage, customErrorResponse.getId(), e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(customErrorResponse);
    }


    @ExceptionHandler({RegistrationDataProcessingException.class})
    public ResponseEntity<Object> handleBadRegistrationRequestException(RegistrationDataProcessingException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_SOME_PARAMETERS_NULL);
        logger.info(loggerMessage, customErrorResponse.getId(), e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(customErrorResponse);
    }

}
