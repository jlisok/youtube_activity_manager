package com.jlisok.youtube_activity_manager.synchronization.services;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface YouTubeDataSynchronizationService {

    CompletableFuture<Void> synchronizeAndSendToCloud(UUID synchronizationId, String googleAccessToken, UUID userId);

}
