package com.jlisok.youtube_activity_manager.youtube.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

@Component
public class GoogleCredentials {

    @Value("${google.scope}")
    private Collection<String> scope;

    @Value("${google.client_secrets}")
    private String clientSecretJson;

    private final JacksonFactory jacksonFactory;
    private final LocalServerReceiver localServerReceiver;


    @Autowired
    public GoogleCredentials(JacksonFactory jacksonFactory, LocalServerReceiver localServerReceiver) {
        this.jacksonFactory = jacksonFactory;
        this.localServerReceiver = localServerReceiver;
    }


    public Credential create(HttpTransport httpTransport, String userId) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(clientSecretJson);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(jacksonFactory, inputStreamReader);
        GoogleAuthorizationCodeFlow flow = googleAuthorizationCodeFlow(googleClientSecrets, httpTransport);
        return new AuthorizationCodeInstalledApp(flow, localServerReceiver)
                .authorize(userId);
    }


    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(GoogleClientSecrets secrets, HttpTransport httpTransport) {
        return new GoogleAuthorizationCodeFlow
                .Builder(httpTransport,
                jacksonFactory,
                secrets,
                scope)
                .build();
    }

}
