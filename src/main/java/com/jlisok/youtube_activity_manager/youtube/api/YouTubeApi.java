package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.jlisok.youtube_activity_manager.youtube.utils.GoogleCredentials;
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
    private final LocalServerReceiver localServerReceiver;
    private final GoogleCredentials googleCredentials;


    @Autowired
    public YouTubeApi(JacksonFactory jacksonFactory, LocalServerReceiver localServerReceiver, GoogleCredentials googleCredentials) {
        this.jacksonFactory = jacksonFactory;
        this.localServerReceiver = localServerReceiver;
        this.googleCredentials = googleCredentials;
    }


    public YouTube get(String userId) throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials = googleCredentials.create(httpTransport, userId);
        return new YouTube
                .Builder(httpTransport, jacksonFactory, credentials)
                .setApplicationName(applicationName)
                .build();
    }
}
