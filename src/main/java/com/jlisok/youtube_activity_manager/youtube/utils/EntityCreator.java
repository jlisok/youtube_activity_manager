package com.jlisok.youtube_activity_manager.youtube.utils;

import com.google.api.services.youtube.model.*;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.models.ChannelBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.models.VideoBuilder;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDtoBuilder;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDtoBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityCreator {

    public static Video createVideo(String videoId, VideoSnippet snippet, VideoContentDetails details, List<String> uriList, Channel channel) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return new VideoBuilder()
                .setId(id)
                .setYouTubeId(videoId)
                .setHashtag(snippet.getTags())
                .setUri(uriList)
                .setDuration(Duration.parse(details.getDuration()))
                .setTitle(snippet.getTitle())
                .setChannel(channel)
                .setPublishedAt(Instant.ofEpochMilli(snippet.getPublishedAt().getValue()))
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createVideo();
    }


    public static Channel createChannel(String youtubeChannelId, ChannelSnippet snippet, ChannelStatistics statistics, ChannelContentOwnerDetails owner, Set<User> userList) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return new ChannelBuilder()
                .setId(id)
                .setYouTubeChannelId(youtubeChannelId)
                .setTitle(snippet.getTitle())
                .setPublishedAt(Instant.ofEpochMilli(snippet.getPublishedAt().getValue()))
                .setLanguage(snippet.getDefaultLanguage())
                .setCountry(snippet.getCountry())
                .setOwner(owner.getContentOwner())
                .setSubscriberNumber(statistics.getSubscriberCount().longValue())
                .setVideoNumber(statistics.getVideoCount().intValue())
                .setViewNumber(statistics.getViewCount().longValue())
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUsers(userList)
                .createChannel();
    }

    public static VideoDto createVideoDto(Video video) {
        return new VideoDtoBuilder()
                .setId(video.getId())
                .setTitle(video.getTitle())
                .setChannelTitle(video.getChannel().getTitle())
                .setDuration(video.getDuration())
                .setDuration(video.getDuration())
                .setPublishedAt(video.getPublishedAt())
                .createVideoDto();
    }


    public static ChannelDto createChannelDto(Channel channel) {
        return new ChannelDtoBuilder()
                .setId(channel.getId())
                .setTitle(channel.getTitle())
                .setSubscriberNumber(channel.getSubscriberNumber())
                .setVideoNumber(channel.getVideoNumber())
                .setViewNumber(channel.getViewNumber())
                .setPublishedAt(channel.getPublishedAt())
                .createChannelDto();
    }
}
