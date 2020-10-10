package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;

import java.io.IOException;
import java.util.List;

public interface YouTubeService {

    List<Channel> listSubscribedChannels() throws IOException;

    List<Video> listRatedVideos(YouTubeRatingDto dto) throws IOException, ExpectedDataNotFoundInDatabase;

}
