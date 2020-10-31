package com.jlisok.youtube_activity_manager.synchronization.utils;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
public class SynchronizationStatusGetter {

    private final SynchronizationRepository synchronizationRepository;

    public SynchronizationStatusGetter(SynchronizationRepository synchronizationRepository) {
        this.synchronizationRepository = synchronizationRepository;
    }

    public SynchronizationStatus getLastSynchronization(UUID userId) {
        Optional<SynchronizationStatus> status = synchronizationRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        return status.orElseGet(SynchronizationStatus::new);
    }

    public Instant getLastSynchronizationTimeWithState(UUID userId, SynchronizationState state) {
        Optional<SynchronizationStatus> status = synchronizationRepository.findFirstByUserIdAndStateOrderByCreatedAtDesc(userId, state);
        return status
                .map(s -> s.getCreatedAt().truncatedTo(ChronoUnit.MINUTES))
                .orElse(null);
    }
}
