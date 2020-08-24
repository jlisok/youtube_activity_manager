package com.jlisok.youtube_activity_manager.domain.exceptions;

import java.time.Instant;

public class CustomException extends Exception {


    private final ExceptionMessage exceptionMessage = new ExceptionMessage();
    private final Throwable throwable;

    public CustomException(String message) {
        exceptionMessage.setMessage(message);
        exceptionMessage.setTimestamp(Instant.now());
        exceptionMessage.setExceptionName(this.getClass().getSimpleName());
        this.throwable = this;
    }

    public CustomException(String message, Throwable throwable) {
        exceptionMessage.setMessage(message);
        exceptionMessage.setTimestamp(Instant.now());
        exceptionMessage.setExceptionName(this.getClass().getSimpleName());
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }
}
