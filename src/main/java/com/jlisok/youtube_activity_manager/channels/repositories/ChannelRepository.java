package com.jlisok.youtube_activity_manager.channels.repositories;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findByUsers_Id(UUID userId);
}


