package com.jlisok.youtube_activity_manager.registration.exceptions;

public class BadRegistrationRequestException extends RegistrationException {


    public BadRegistrationRequestException(String message) {
        super(message);
    }

    public BadRegistrationRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
