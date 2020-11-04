package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.services.DemoUserAuthenticationService;
import com.jlisok.youtube_activity_manager.login.services.GoogleAuthorizationService;
import com.jlisok.youtube_activity_manager.login.services.GoogleLoginService;
import com.jlisok.youtube_activity_manager.login.services.TraditionalLoginService;
import com.jlisok.youtube_activity_manager.synchronization.services.YouTubeDataSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.FailedLoginException;
import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("api/v1/login")
public class LoginController {

    private final TraditionalLoginService traditionalLoginService;
    private final GoogleLoginService googleLoginService;
    private final GoogleAuthorizationService googleAuthorizationService;
    private final YouTubeDataSynchronizationService synchronizationService;
    private final DemoUserAuthenticationService demoUserAuthenticationService;


    @Autowired
    public LoginController(TraditionalLoginService traditionalLoginService, GoogleLoginService googleLoginService, GoogleAuthorizationService googleAuthorizationService, YouTubeDataSynchronizationService synchronizationService, DemoUserAuthenticationService demoUserAuthenticationService) {
        this.traditionalLoginService = traditionalLoginService;
        this.googleLoginService = googleLoginService;
        this.googleAuthorizationService = googleAuthorizationService;
        this.synchronizationService = synchronizationService;
        this.demoUserAuthenticationService = demoUserAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws FailedLoginException {
        return ResponseEntity
                .ok()
                .body(traditionalLoginService.authenticateUser(loginRequestDto));
    }


    @PostMapping("/demoUser")
    public ResponseEntity<String> authenticateDemoUser() {
        return ResponseEntity
                .ok()
                .body(demoUserAuthenticationService.authenticateUser());
    }


    // Note, that authentication and synchronization processes are run within two separate transactions
    @PostMapping("/viaGoogle")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody GoogleRequestDto loginRequestDto) throws GeneralSecurityException, IOException {
        AuthenticationDto dto = googleLoginService.authenticateUser(loginRequestDto);
        synchronizationService.synchronizeAndSendToCloud(loginRequestDto.getAccessToken(), dto.getUserId()); //asynchronous call
        return ResponseEntity
                .ok()
                .body(dto.getJwtToken());
    }

    // Note, that authentication and synchronization processes are run within two separate transactions
    @PostMapping("/authorize")
    public ResponseEntity<String> authorizeUser(@Valid @RequestBody GoogleRequestDto loginRequestDto) throws GeneralSecurityException, IOException {
        AuthenticationDto dto = googleAuthorizationService.authorizeUser(loginRequestDto);
        synchronizationService.synchronizeAndSendToCloud(loginRequestDto.getAccessToken(), dto.getUserId()); //asynchronous call
        return ResponseEntity
                .ok()
                .body(dto.getJwtToken());
    }
}
