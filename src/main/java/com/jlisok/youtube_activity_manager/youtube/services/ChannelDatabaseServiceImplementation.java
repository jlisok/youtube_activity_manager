package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.channel.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChannelDatabaseServiceImplementation implements ChannelDatabaseService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelDatabaseServiceImplementation(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void updateChannelsInDatabase(List<Channel> channels, UUID userId) {
        Map<String, Channel> repositoryChannels = fetchAllChannelsRelatedTo(userId);
        List<Channel> readyToInsertChannels = channels
                .stream()
                .map(channel -> createOrUpdateChannel(channel, repositoryChannels))
                .collect(Collectors.toList());
        channelRepository.saveAll(readyToInsertChannels);
        channelRepository.flush();
    }


    private Map<String, Channel> fetchAllChannelsRelatedTo(UUID userId) {
        List<Channel> channelList = channelRepository.findByUsers_Id(userId);
        return MapCreator.toMap(channelList, Channel::getYouTubeChannelId, Function.identity());
    }


    private Channel createOrUpdateChannel(Channel newChannelEntity, Map<String, Channel> repositoryChannels) {
        if (repositoryChannels.isEmpty()) {
            return newChannelEntity;
        }
        return Optional.ofNullable(repositoryChannels.get(newChannelEntity.getYouTubeChannelId()))
                       .map(oldChannelEntity -> updateChannel(oldChannelEntity, newChannelEntity))
                       .orElse(newChannelEntity);
    }


    private Channel updateChannel(Channel oldChannelEntity, Channel newChannelEntity) {
        Set<User> users = oldChannelEntity.getUsers();
        users.addAll(newChannelEntity.getUsers());
        newChannelEntity.setId(oldChannelEntity.getId());
        newChannelEntity.setCreatedAt(oldChannelEntity.getCreatedAt());
        newChannelEntity.setUsers(users);
        return newChannelEntity;
    }
}
