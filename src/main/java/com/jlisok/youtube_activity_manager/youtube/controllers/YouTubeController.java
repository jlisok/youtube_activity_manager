package com.jlisok.youtube_activity_manager.youtube.controllers;

import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeVideoDto;
import com.jlisok.youtube_activity_manager.youtube.services.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
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
    public List<Video> getRatedVideos(@Valid @RequestBody YouTubeVideoDto dto) {
        service.listRatedVideos(dto);
    }


}
