package com.jlisok.youtube_activity_manager.synchronization.repositories;

import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface SynchronizationRepository extends JpaRepository<SynchronizationStatus, UUID> {

    Optional<SynchronizationStatus> findByUserId(UUID userId);

    @Transactional
    void deleteByUserId(UUID id);

}
