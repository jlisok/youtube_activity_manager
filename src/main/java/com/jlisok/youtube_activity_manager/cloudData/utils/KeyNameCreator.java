package com.jlisok.youtube_activity_manager.cloudData.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyNameCreator {

    public String createKeyName(UUID userId) {
        return userId.toString() + ".json";
    }
}
