package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.utils.UserFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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


    private Channel translateToChannel(com.google.api.services.youtube.model.Channel youtubeChannel, UUID userId) throws ExpectedDataNotFoundInDatabase {
        User user = userFetcher.fetchUser(userId);
        return createChannel(youtubeChannel.getId(), youtubeChannel.getSnippet(), youtubeChannel
                .getStatistics(), youtubeChannel.getContentOwnerDetails(), Collections.singleton(user));
    }

}
