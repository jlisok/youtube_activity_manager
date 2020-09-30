package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.services.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
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
    public ResponseEntity<List<Video>> getRatedVideos(@Valid @RequestBody YouTubeRatingDto dto) throws IOException, ExpectedDataNotFoundInDatabase {
        return ResponseEntity
                .ok()
                .body(service.listRatedVideos(dto));
    }

    @GetMapping("/channels")
    public ResponseEntity<List<Subscription>> getSubscribedChannels() throws IOException {
        return ResponseEntity
                .ok()
                .body(service.listSubscribedChannels());
    }


}
