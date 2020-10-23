package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import com.jlisok.youtube_activity_manager.synchronization.utils.SynchronizationStatusGetter;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.UserActivityDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Service
public class UserActivityServiceImplementation implements UserActivityService {

    private final UserVideoRepository userVideoRepository;
    private final ChannelRepository channelRepository;
    private final SynchronizationStatusGetter synchronizationStatusGetter;

    @Autowired
    public UserActivityServiceImplementation(UserVideoRepository userVideoRepository, ChannelRepository channelRepository, SynchronizationStatusGetter synchronizationStatusGetter) {
        this.userVideoRepository = userVideoRepository;
        this.channelRepository = channelRepository;
        this.synchronizationStatusGetter = synchronizationStatusGetter;
    }


    @Override
    public UserActivityDto<VideoDto> getRatedVideos(Rating rating) {
        UUID userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
        List<UserVideo> userVideos = userVideoRepository.findByUserIdAndRating(userId, rating);
        var videoDto = userVideos
                .stream()
                .map(userVideo -> {
                    var video = userVideo.getVideo();
                    return EntityCreator.createVideoDto(video);
                })
                .collect(Collectors.toList());
        var lastStatus = synchronizationStatusGetter.getLastSynchronization(userId);
        return new UserActivityDto<>(videoDto, lastStatus.getStatus(), lastStatus.getCreatedAt());
    }


    @Override
    public UserActivityDto<ChannelDto> getSubscribedChannels() {
        UUID userId = getAuthenticationInContext().getAuthenticatedUserId();
        List<Channel> channels = channelRepository.findByUsers_Id(userId);
        var channelDto = channels
                .stream()
                .map(EntityCreator::createChannelDto)
                .collect(Collectors.toList());
        var lastStatus = synchronizationStatusGetter.getLastSynchronization(userId);
        return new UserActivityDto<>(channelDto, lastStatus.getStatus(), lastStatus.getCreatedAt());
    }
}
