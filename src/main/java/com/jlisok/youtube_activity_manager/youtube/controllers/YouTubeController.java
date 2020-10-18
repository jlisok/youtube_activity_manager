package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.services.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/youtube")
public class YouTubeController {

    private final UserActivityService service;

    @Autowired
    public YouTubeController(UserActivityService service) {
        this.service = service;
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoDto>> getRatedVideos(@Valid YouTubeRatingDto dto) throws ExpectedDataNotFoundInDatabase, IOException {
        return ResponseEntity
                .ok()
                .body(service.getRatedVideos(dto));
    }

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelDto>> getSubscribedChannels() throws Exception {
        return ResponseEntity
                .ok()
                .body(service.getSubscribedChannels());
    }
}
