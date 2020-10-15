package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    List<Channel> createChannels(List<com.google.api.services.youtube.model.Channel> channelList, UUID userId);

}
