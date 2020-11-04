package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public interface GoogleAuthorizationService {

    AuthenticationDto authorizeUser(GoogleRequestDto dto) throws GeneralSecurityException, IOException;

}
