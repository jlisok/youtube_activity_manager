package com.jlisok.youtube_activity_manager.security.configs;

import java.time.Duration;

public class JwtSecurityConstants {

    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final Duration DURATION = Duration.ofMinutes(90);
    public static final String ISSUER = "com.jlisok.youtube_activity_manager";
    public static final String HEADER_START_SCHEMA =  "Bearer: ";
}
