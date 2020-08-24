package com.jlisok.youtube_activity_manager.domain.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.HEADER_START_SCHEMA;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtSubjectDecoder {


    public static UUID getUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            String token = authHeader.substring(HEADER_START_SCHEMA.length());
            DecodedJWT decodedJWT = JWT.decode(token);
            return UUID.fromString(decodedJWT.getSubject());
        } catch (Exception e) {
            throw new JWTDecodeException("Jwt processing failed. Could not parse subject to User Id.", e);
        }
    }

}
