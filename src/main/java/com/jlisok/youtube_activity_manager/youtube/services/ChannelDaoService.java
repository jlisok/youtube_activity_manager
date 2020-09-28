package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelDaoService {

    void updateChannelsInDatabase(List<Channel> channels, UUID userId);

}
