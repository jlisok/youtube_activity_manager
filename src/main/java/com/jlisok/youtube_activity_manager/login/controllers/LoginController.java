package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.FailedLoginException;
import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/login")
@CrossOrigin
public class LoginController {

    private final LoginService loginService;


    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public String authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws FailedLoginException {
        return loginService.authenticateUser(loginRequestDto);
    }
}
