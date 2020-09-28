package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.BaseExceptionHandler;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.registration.exceptions.FieldViolationBadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationDataProcessingException;
import com.jlisok.youtube_activity_manager.registration.exceptions.UnexpectedErrorBadRegistrationRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RegistrationControllerAdvice extends BaseExceptionHandler {

    @Override
    protected Logger getLoggerInstance() {
        return LoggerFactory.getLogger(this.getClass());
    }

    @Override
    protected String getLoggerMessage() {
        return "{} Registration failed.";
    }


    @ExceptionHandler({PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException.class})
    public ResponseEntity<Object> handleFailedLoginException(PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.REGISTRATION_FAILED_PREFIX_PHONE_NUMBER_MUST_BE_FILLED_IN_OR_NULL, HttpStatus.BAD_REQUEST, exception);
    }


    @ExceptionHandler({FieldViolationBadRegistrationRequestException.class})
    public ResponseEntity<Object> handleFailedLoginException(FieldViolationBadRegistrationRequestException exception) {
        return handleExceptionWithInfoLogging(ResponseCode.REGISTRATION_FAILED_VIOLATED_FIELD_EMAIL, HttpStatus.BAD_REQUEST, exception);
    }


    @ExceptionHandler({UnexpectedErrorBadRegistrationRequestException.class})
    public ResponseEntity<Object> handleFailedLoginException(UnexpectedErrorBadRegistrationRequestException exception) {
        return handleExceptionWithErrorLogging(ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }


    @ExceptionHandler({RegistrationDataProcessingException.class})
    public ResponseEntity<Object> handleFailedLoginException(RegistrationDataProcessingException exception) {
        return handleExceptionWithErrorLogging(ResponseCode.REGISTRATION_FAILED_SOME_PARAMETERS_NULL, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

}
