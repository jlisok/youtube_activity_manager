package com.jlisok.youtube_activity_manager.domain.exceptions;

import java.time.Instant;

public class ExceptionMessage {

    private Instant timestamp = Instant.now();
    private String message;
    private String exceptionName;


    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
