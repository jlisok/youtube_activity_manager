package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class YouTubeBuilder {

    @Value("${google.application_name}")
    private String applicationName;

    private final JacksonFactory jacksonFactory;
    private final NetHttpTransport netHttpTransport;

    @Autowired
    public YouTubeBuilder(JacksonFactory jacksonFactory, NetHttpTransport netHttpTransport) {
        this.jacksonFactory = jacksonFactory;
        this.netHttpTransport = netHttpTransport;
    }


    public YouTube get(String accessToken) {
        GoogleCredential credential = new GoogleCredential();
        credential.setAccessToken(accessToken);
        return new YouTube
                .Builder(netHttpTransport, jacksonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }
}
