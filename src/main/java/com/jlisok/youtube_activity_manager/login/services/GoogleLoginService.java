package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleLoginService {

    AuthenticationDto authenticateUser(GoogleLoginRequestDto dto)throws GeneralSecurityException, IOException;
}
