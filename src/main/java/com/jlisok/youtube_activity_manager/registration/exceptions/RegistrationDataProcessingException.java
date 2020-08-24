package com.jlisok.youtube_activity_manager.registration.exceptions;

public class RegistrationDataProcessingException extends RegistrationException {

    public RegistrationDataProcessingException(String message) {
        super(message);
    }

    public RegistrationDataProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}