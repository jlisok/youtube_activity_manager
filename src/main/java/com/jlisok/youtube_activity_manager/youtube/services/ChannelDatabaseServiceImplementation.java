package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.youtube.utils.IdsFetcher;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelDatabaseServiceImplementation implements ChannelDatabaseService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelDatabaseServiceImplementation(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    @Transactional
    public List<Channel> updateChannelsInDatabase(List<Channel> channels, UUID userId) {
        List<String> channelIds = IdsFetcher.getIdsFrom(channels, Channel::getYouTubeChannelId);
        Map<String, Channel> repositoryChannels = fetchChannels(channelIds);
        List<Channel> readyToInsertChannels = channels
                .stream()
                .map(channel -> createOrUpdateChannel(channel, repositoryChannels))
                .collect(Collectors.toList());

        List<Channel> savedChannels = channelRepository.saveAll(readyToInsertChannels);
        channelRepository.flush();
        return savedChannels;
    }


    private Map<String, Channel> fetchChannels(List<String> channelIds) {
        List<Channel> channelList = channelRepository.findAllByYouTubeChannelIdIn(channelIds);
        return MapCreator.toMap(channelList, Channel::getYouTubeChannelId);
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
