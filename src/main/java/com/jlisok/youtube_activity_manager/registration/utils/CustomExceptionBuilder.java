package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class CustomExceptionBuilder {

    public static void handleHibernateExceptionFromNestedStack(DataIntegrityViolationException e) throws BadRegistrationRequestException {

        ResponseCode responseCode;
        HttpStatus httpResponseStatus;
        String detailedMessage;
        CustomErrorResponse customErrorResponse;

        if (e.getMostSpecificCause() instanceof PSQLException) {
            PSQLException mostSpecificCause = (PSQLException) e.getMostSpecificCause();
            String psqlState = mostSpecificCause.getSQLState();
            String message = mostSpecificCause.getMessage();

            if (message.contains("email")) {
                httpResponseStatus = BAD_REQUEST;
                responseCode = ResponseCode.REGISTRATION_FAILED_VIOLATED_FIELD_EMAIL;
            } else {
                httpResponseStatus = INTERNAL_SERVER_ERROR;
                responseCode = ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR;
            }
            detailedMessage = "PSQLState: " + psqlState;
            customErrorResponse = new CustomErrorResponse(responseCode, httpResponseStatus, detailedMessage);
        } else {
            httpResponseStatus = INTERNAL_SERVER_ERROR;
            responseCode = ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR;
            customErrorResponse = new CustomErrorResponse(responseCode, httpResponseStatus);
        }

        throw new BadRegistrationRequestException(e.getMessage(), e, customErrorResponse);
    }

}
