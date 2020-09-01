package com.jlisok.youtube_activity_manager.aboutus.services;

import com.jlisok.youtube_activity_manager.aboutus.configurations.AboutUsServiceConfiguration;
import com.jlisok.youtube_activity_manager.aboutus.dto.AboutUsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(AboutUsServiceConfiguration.class)
public class AboutUsService {

    private final AboutUsDto aboutUsDto;

    @Autowired
    public AboutUsService(AboutUsServiceConfiguration aboutUsServiceConfiguration) {
        aboutUsDto = new AboutUsDto(aboutUsServiceConfiguration.getVersion(), aboutUsServiceConfiguration.getEmail());
    }

    public AboutUsDto getAboutUsDto() {
        return aboutUsDto;
    }
}

