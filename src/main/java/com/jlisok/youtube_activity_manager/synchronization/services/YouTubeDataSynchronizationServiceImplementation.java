package com.jlisok.youtube_activity_manager.synchronization.services;

import com.jlisok.youtube_activity_manager.cloudData.services.WritingAndSendingService;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.services.YouTubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class YouTubeDataSynchronizationServiceImplementation implements YouTubeDataSynchronizationService {

    private final YouTubeService youTubeService;
    private final SynchronizationRepository repository;
    private final UserVideoRepository userVideoRepository;
    private final WritingAndSendingService<List<Video>> awsService;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public YouTubeDataSynchronizationServiceImplementation(YouTubeService youTubeService, SynchronizationRepository repository, UserVideoRepository userVideoRepository, WritingAndSendingService<List<Video>> awsService, EntityManager entityManager) {
        this.youTubeService = youTubeService;
        this.repository = repository;
        this.userVideoRepository = userVideoRepository;
        this.awsService = awsService;
        this.entityManager = entityManager;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> synchronizeAndSendToCloud(String accessToken, UUID userId) {
        logger.debug("Synchronization service - initialization");
        UUID synchronizationId = UUID.randomUUID();
        return synchronizeYouTubeData(synchronizationId, accessToken, userId)
                .thenCompose(i -> sendDataToCloud(userId));
    }


    public CompletableFuture<Void> synchronizeYouTubeData(UUID synchronizationId, String accessToken, UUID userId) {
        return CompletableFuture.supplyAsync(() -> insertStatus(synchronizationId, userId, SynchronizationState.IN_PROGRESS))
                                .thenApply(i -> fetchAndInsertYouTubeData(accessToken, userId))
                                .thenAccept(i -> insertStatus(synchronizationId, userId, SynchronizationState.SUCCEEDED))
                                .exceptionally(e -> insertStatusOnFail(synchronizationId, userId, e))
                                .orTimeout(1, TimeUnit.MINUTES);
    }


    public CompletableFuture<Void> sendDataToCloud(UUID userId) {
        return CompletableFuture.supplyAsync(() -> prepareAndSendDataToCloud(userId))
                                .exceptionally(e -> {
                                    logger.error("Failed while sending data to Aws for userId: {}", userId, e);
                                    return null;
                                })
                                .orTimeout(1, TimeUnit.MINUTES);
    }


    private Void fetchAndInsertYouTubeData(String accessToken, UUID userId) {
        logger.debug("Synchronization service - fetching data");
        youTubeService.synchronizeRatedVideos(accessToken, userId, Rating.LIKE);
        youTubeService.synchronizeRatedVideos(accessToken, userId, Rating.DISLIKE);
        youTubeService.synchronizeSubscribedChannels(accessToken, userId);
        logger.debug("Synchronization service - fetching data - success");
        return null;
    }


    private Void insertStatus(UUID id, UUID userId, SynchronizationState state) {
        Instant now = Instant.now();
        User user = entityManager.getReference(User.class, userId);
        SynchronizationStatus status = new SynchronizationStatus(id, state, now, user);
        repository.save(status);
        return null;
    }


    private Void insertStatusOnFail(UUID id, UUID userId, Throwable e) {
        insertStatus(id, userId, SynchronizationState.FAILED);
        logger.error("Failed while fetching and inserting youtubeApi data for userId: {}", userId, e);
        return null;
    }


    private Void prepareAndSendDataToCloud(UUID userId) {
        logger.debug("Synchronization service - preparing and sending data to AWS");
        List<UserVideo> userVideos = userVideoRepository.findByUserId(userId);
        List<Video> videos = userVideos
                .stream()
                .map(UserVideo::getVideo)
                .collect(Collectors.toList());
        awsService.writeAndSendData(videos, userId);
        logger.debug("Synchronization service - preparing and sending data to AWS - success");
        return null;
    }
}
