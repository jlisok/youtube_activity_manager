package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;

import java.io.IOException;
import java.util.List;

public interface UserActivityService {

    List<VideoDto> getRatedVideos(YouTubeRatingDto dto) throws IOException;

    List<ChannelDto> getSubscribedChannels() throws IOException;
}
