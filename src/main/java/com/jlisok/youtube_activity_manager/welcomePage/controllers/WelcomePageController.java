package com.jlisok.youtube_activity_manager.welcomePage.controllers;

import com.jlisok.youtube_activity_manager.welcomePage.services.WelcomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/welcome")
public class WelcomePageController {

    private final WelcomePageService welcomePageService;

    @Autowired
    public WelcomePageController(WelcomePageService welcomePageService) {
        this.welcomePageService = welcomePageService;
    }

    @GetMapping
    public boolean isAuthorized(HttpServletRequest request) {
        return welcomePageService.isConnectedToGoogleAccount(request);
    }
}
