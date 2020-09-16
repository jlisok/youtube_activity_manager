package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeListDto;
import com.jlisok.youtube_activity_manager.youtube.services.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
@RequestMapping("api/v1/youtube")
public class YouTubeController {

    private final YouTubeService service;

    @Autowired
    public YouTubeController(YouTubeService service) {
        this.service = service;
    }

    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getRatedVideos(@Valid @RequestBody YouTubeListDto dto) throws IOException, GeneralSecurityException {
        return ResponseEntity
                .ok()
                .body(service.listRatedVideos(dto));
    }

    @GetMapping("/channels")
    public ResponseEntity<List<Subscription>> getSubscribedChannels(@Valid @RequestBody YouTubeListDto dto) throws IOException, GeneralSecurityException {
        return ResponseEntity
                .ok()
                .body(service.listOfChannels(dto));
    }


}
