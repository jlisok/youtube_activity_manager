package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.services.GoogleLoginService;
import com.jlisok.youtube_activity_manager.login.services.GoogleLoginServiceImplementation;
import com.jlisok.youtube_activity_manager.login.services.TraditionalLoginService;
import com.jlisok.youtube_activity_manager.login.services.TraditionalLoginServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public LoginController(TraditionalLoginServiceImplementation traditionalLoginService, GoogleLoginServiceImplementation googleLoginService) {
        this.traditionalLoginService = traditionalLoginService;
        this.googleLoginService = googleLoginService;
    }

    @PostMapping
    public String authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws FailedLoginException {
        return traditionalLoginService.authenticateUser(loginRequestDto);
    }

    @PostMapping("/viaGoogle")
    public String authenticateUser(@Valid @RequestBody GoogleLoginRequestDto loginRequestDto) throws GeneralSecurityException, IOException {
        return googleLoginService.authenticateUser(loginRequestDto);
    }
}
