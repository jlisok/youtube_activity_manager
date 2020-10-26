package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface TestChannelRepository extends JpaRepository<Channel, UUID> {

    @Transactional
    void deleteAllByUsers_Id(UUID userId);
}


