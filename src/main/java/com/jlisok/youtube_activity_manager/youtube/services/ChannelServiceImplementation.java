package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.utils.UserFetcher;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator.createChannel;

@Service
public class ChannelServiceImplementation implements ChannelService {

    private final UserFetcher userFetcher;

    @Autowired
    public ChannelServiceImplementation(UserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    @Override
    public List<Channel> createChannels(List<com.google.api.services.youtube.model.Channel> channelList, UUID userId) {
        return channelList
                .stream()
                .map(channel -> translateToChannel(channel, userId))
                .collect(Collectors.toList());
    }


    private Channel translateToChannel(com.google.api.services.youtube.model.Channel youtubeChannel, UUID userId) {
        Set<User> users = Optional
                .ofNullable(userId)
                .stream()
                .map(userFetcher::fetchUser)
                .collect(Collectors.toSet());

        return EntityCreator.createChannel(youtubeChannel.getId(), youtubeChannel.getSnippet(), youtubeChannel
                .getStatistics(), youtubeChannel.getContentOwnerDetails(), users);
    }

}
