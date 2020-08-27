package com.jlisok.youtube_activity_manager.registration.exceptions;

public class FieldViolationBadRegistrationRequestException extends BadRegistrationRequestException {

    public FieldViolationBadRegistrationRequestException(String message, Throwable throwable, String userDetailedMessage) {
        super(message, throwable, userDetailedMessage);
    }
}
