package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class YouTubeApi {

    @Value("${google.application_name}")
    private String applicationName;

    private final JacksonFactory jacksonFactory;


    @Autowired
    public YouTubeApi(JacksonFactory jacksonFactory) {
        this.jacksonFactory = jacksonFactory;
    }


    public YouTube get(String accessToken) throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential();
        credential.setAccessToken(accessToken);
        return new YouTube
                .Builder(httpTransport, jacksonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }
}
