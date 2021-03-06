package com.jlisok.youtube_activity_manager.synchronization.repositories;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SynchronizationRepository extends JpaRepository<SynchronizationStatus, UUID> {

    Optional<SynchronizationStatus> findByUserId(UUID userId);

    Optional<SynchronizationStatus> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<SynchronizationStatus> findFirstByUserIdAndStateOrderByCreatedAtDesc(UUID userId, SynchronizationState state);

    @Transactional
    void deleteByUserId(UUID id);

}
