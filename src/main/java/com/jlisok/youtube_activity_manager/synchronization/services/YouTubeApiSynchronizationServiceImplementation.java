package com.jlisok.youtube_activity_manager.synchronization.services;

import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.utils.SynchronizationStatusGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class YouTubeApiSynchronizationServiceImplementation implements YouTubeApiSynchronizationService {

    private final SynchronizationStatusGetter getter;

    @Autowired
    public YouTubeApiSynchronizationServiceImplementation(SynchronizationStatusGetter getter) {
        this.getter = getter;
    }

    @Override
    public Instant getLastSuccessfulSynchronization() {
        UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
        return getter.getLastSynchronizationTimeWithState(userId, SynchronizationState.SUCCEEDED);
    }
}
