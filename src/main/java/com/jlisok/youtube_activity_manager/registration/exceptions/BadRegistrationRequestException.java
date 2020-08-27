package com.jlisok.youtube_activity_manager.registration.exceptions;

public class BadRegistrationRequestException extends RegistrationException {

    private final String userDetailedMessage;


    public BadRegistrationRequestException(String message, Throwable throwable, String userDetailedMessage) {
        super(message, throwable);
        this.userDetailedMessage = userDetailedMessage;
    }


    public String getUserDetailedMessage() {
        return userDetailedMessage;
    }

}
