package com.jlisok.youtube_activity_manager.synchronization.controllers;

import com.jlisok.youtube_activity_manager.synchronization.services.YouTubeApiSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("api/v1/synchronization")
public class SynchronizationController {

    private final YouTubeApiSynchronizationService service;

    @Autowired
    public SynchronizationController(YouTubeApiSynchronizationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Instant> getTimestampOfLastSuccessfulSynchronization() {
        return ResponseEntity
                .ok()
                .body(service.getLastSuccessfulSynchronization());
    }
}
