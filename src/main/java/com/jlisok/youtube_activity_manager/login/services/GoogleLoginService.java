package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;

public interface GoogleLoginService {

    String authenticateUser(GoogleLoginRequestDto dto) throws Exception;
}
