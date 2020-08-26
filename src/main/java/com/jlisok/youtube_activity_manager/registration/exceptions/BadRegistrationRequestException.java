package com.jlisok.youtube_activity_manager.registration.exceptions;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;

public class BadRegistrationRequestException extends RegistrationException {

    private final CustomErrorResponse customErrorResponse;


    public BadRegistrationRequestException(String message, Throwable throwable, CustomErrorResponse customErrorResponse) {
        super(message, throwable);
        this.customErrorResponse = customErrorResponse;
    }


    public CustomErrorResponse getCustomErrorResponse() {
        return customErrorResponse;
    }

}
