package com.jlisok.youtube_activity_manager.login.exceptions;

public class GoogleIdsDoNotMatchException extends AuthorizationException {

    public GoogleIdsDoNotMatchException(String message) {
        super(message);
    }
}
