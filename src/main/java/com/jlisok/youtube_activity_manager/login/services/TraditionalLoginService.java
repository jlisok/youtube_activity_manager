package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;

import javax.security.auth.login.FailedLoginException;

public interface TraditionalLoginService {

    String authenticateUser(LoginRequestDto dto) throws FailedLoginException;
}
