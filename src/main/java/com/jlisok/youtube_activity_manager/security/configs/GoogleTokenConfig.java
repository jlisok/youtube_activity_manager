package com.jlisok.youtube_activity_manager.security.configs;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleTokenConfig {

    @Value("${google.client_id}")
    private String clientId;


    @Bean
    public GoogleIdTokenVerifier createGoogleTokenVerifier() throws GeneralSecurityException, IOException {
        return new GoogleIdTokenVerifier
                .Builder(netHttpTransport(), jacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }


    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }


    @Bean
    public JacksonFactory jacksonFactory() {
        return new JacksonFactory();
    }
}
