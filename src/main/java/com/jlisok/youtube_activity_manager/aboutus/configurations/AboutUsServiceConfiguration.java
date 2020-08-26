package com.jlisok.youtube_activity_manager.aboutus.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AboutUsServiceConfiguration {

    @Value("${aboutus.version}")
    private String version;

    @Value("${aboutus.email}")
    private String email;

    public String getVersion() {
        return version;
    }

    public String getEmail() {
        return email;
    }

}
