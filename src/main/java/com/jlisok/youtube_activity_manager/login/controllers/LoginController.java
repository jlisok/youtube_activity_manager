package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.services.GoogleLoginService;
import com.jlisok.youtube_activity_manager.login.services.TraditionalLoginService;
import com.jlisok.youtube_activity_manager.synchronization.services.YouTubeDataSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.FailedLoginException;
import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/login")
public class LoginController {

    private final TraditionalLoginService traditionalLoginService;
    private final GoogleLoginService googleLoginService;
    private final YouTubeDataSynchronizationService synchronizationService;


    @Autowired
    public LoginController(TraditionalLoginService traditionalLoginService, GoogleLoginService googleLoginService, YouTubeDataSynchronizationService synchronizationService) {
        this.traditionalLoginService = traditionalLoginService;
        this.googleLoginService = googleLoginService;
        this.synchronizationService = synchronizationService;
    }

    @PostMapping
    public String authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws FailedLoginException {
        return traditionalLoginService.authenticateUser(loginRequestDto);
    }

    @PostMapping("/viaGoogle")
    public String authenticateUser(@Valid @RequestBody GoogleLoginRequestDto loginRequestDto) throws GeneralSecurityException, IOException {
        AuthenticationDto dto = googleLoginService.authenticateUser(loginRequestDto);
        UUID synchronizationId = UUID.randomUUID();
        synchronizationService.synchronizeAndSendToCloud(synchronizationId, loginRequestDto.getAccessToken(), dto.getUserId()); //asynchronous call
        return dto.getJwtToken();
    }
}
