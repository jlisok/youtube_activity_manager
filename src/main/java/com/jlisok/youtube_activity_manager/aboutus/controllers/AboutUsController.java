package com.jlisok.youtube_activity_manager.aboutus.controllers;

import com.jlisok.youtube_activity_manager.aboutus.dto.AboutUsDto;
import com.jlisok.youtube_activity_manager.aboutus.services.AboutUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/aboutus")
public class AboutUsController {

    private final AboutUsService service;

    @Autowired
    public AboutUsController(AboutUsService service) {
        this.service = service;
    }

    @GetMapping
    public AboutUsDto getPageContent() {
        return service.getAboutUsDto();
    }

}
