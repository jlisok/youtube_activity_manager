package com.jlisok.youtube_activity_manager.registration.exceptions;

public class UnexpectedErrorBadRegistrationRequestException extends BadRegistrationRequestException {

    public UnexpectedErrorBadRegistrationRequestException(String message, Throwable throwable, String userDetailedMessage) {
        super(message, throwable, userDetailedMessage);
    }
}
