package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActivityServiceImplementation implements UserActivityService {

    private final YouTubeService service;

    @Autowired
    public UserActivityServiceImplementation(YouTubeService service) {
        this.service = service;
    }


    @Override
    public List<VideoDto> getRatedVideos(YouTubeRatingDto dto) throws IOException {
        List<Video> videos = service.listRatedVideos(dto);
        return videos
                .stream()
                .map(EntityCreator::createVideoDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<ChannelDto> getSubscribedChannels() throws IOException {
        List<Channel> channels = service.listSubscribedChannels();
        return channels
                .stream()
                .map(EntityCreator::createChannelDto)
                .collect(Collectors.toList());
    }
}
