package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class MockGoogleIdToken {

    @Value("${google.client_id}")
    private static String client_id;

    public static GoogleIdToken createDummyGoogleIdToken(String email, Boolean ifEmailVerified, Boolean ifFirstNamePresent) {
        Header header = createHeader();
        Payload payload = createPayload(email, ifEmailVerified, ifFirstNamePresent);
        byte[] signatureByte = new byte[1];
        return new GoogleIdToken(header, payload, signatureByte, signatureByte);
    }


    private static Header createHeader() {
        Instant issuedAtTime = Instant
                .now()
                .minus(Duration.ofMinutes(30));
        Instant expirationTime = Instant
                .now()
                .plus(Duration.ofMinutes(30));
        Header header = new Header();
        header.set("iat", issuedAtTime);
        header.set("exp", expirationTime);
        header.set("iss", "https://accounts.google.com");
        header.set("azp", client_id);
        header.set("aud", client_id);
        return header;
    }


    private static Payload createPayload(String email, Boolean ifEmailVerified, Boolean ifFirstNamePresent) {
        String googleId = String.valueOf(new Random().nextLong());
        Payload payload = new GoogleIdToken.Payload();
        payload.setEmail(email);
        payload.setSubject(googleId);
        payload.setEmailVerified(ifEmailVerified);
        if (ifFirstNamePresent){
            payload.set("given_name", "Joe");
        }
        payload.set("last_name", "Doe");
        return payload;
    }
}
