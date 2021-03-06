package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.UserActivityDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;

import java.io.IOException;
import java.util.List;

public interface UserActivityService {

    UserActivityDto<VideoDto> getRatedVideos(Rating rating);

    UserActivityDto<ChannelDto> getSubscribedChannels();
}
