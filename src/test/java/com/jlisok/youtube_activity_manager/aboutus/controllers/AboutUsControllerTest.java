package com.jlisok.youtube_activity_manager.aboutus.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.aboutus.configurations.AboutUsServiceConfiguration;
import com.jlisok.youtube_activity_manager.aboutus.dto.AboutUsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AboutUsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AboutUsServiceConfiguration defaultProperties;

    @Autowired
    private ObjectMapper om;


    @Test
    void getPageContent() throws Exception {
        final var aboutUsDto = new AboutUsDto(defaultProperties.getVersion(), defaultProperties.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/aboutus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(om.writeValueAsString(aboutUsDto)));
    }


}