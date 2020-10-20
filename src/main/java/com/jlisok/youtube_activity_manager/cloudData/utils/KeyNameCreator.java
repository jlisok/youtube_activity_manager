package com.jlisok.youtube_activity_manager.cloudData.utils;

import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import org.springframework.stereotype.Component;

@Component
public class KeyNameCreator {

    public String createKeyName() {
        return JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId().toString() + ".json";
    }
}
