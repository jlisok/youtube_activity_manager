package com.jlisok.youtube_activity_manager.registration.exceptions;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomException;

public class RegistrationException extends CustomException {


    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
