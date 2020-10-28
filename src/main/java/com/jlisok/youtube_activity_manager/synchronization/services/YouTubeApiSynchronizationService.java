package com.jlisok.youtube_activity_manager.synchronization.services;

import java.time.Instant;

public interface YouTubeApiSynchronizationService {

    Instant getLastSuccessfulSynchronization();

}
