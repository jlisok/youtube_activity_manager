package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YouTubeEntityVerifier {

    public static void assertVideoDtoNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("title"));
        assertTrue(response.contains("channelTitle"));
        assertTrue(response.contains("duration"));
        assertTrue(response.contains("publishedAt"));
    }


    public static void assertChannelDtoNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("title"));
        assertTrue(response.contains("viewNumber"));
        assertTrue(response.contains("subscriberNumber"));
        assertTrue(response.contains("videoNumber"));
        assertTrue(response.contains("publishedAt"));
    }


    public static void assertListOfChannelsEqual(List<com.google.api.services.youtube.model.Channel> youTubeList, List<Channel> actualChannelList) {
        assertEquals(youTubeList.size(), actualChannelList.size());
        for (int i = 0; i < actualChannelList.size(); i++) {
            com.google.api.services.youtube.model.Channel youtubeChannel = youTubeList.get(i);
            Channel channel = actualChannelList.get(i);
            assertEquals(youtubeChannel.getSnippet().getCountry(), channel.getCountry());
            assertEquals(youtubeChannel.getSnippet().getDefaultLanguage(), channel.getLanguage());
            assertEquals(youtubeChannel.getSnippet().getTitle(), channel.getTitle());
            assertEquals(youtubeChannel.getStatistics().getVideoCount().intValue(), channel.getVideoNumber());
            assertEquals(youtubeChannel.getStatistics().getVideoCount().intValue(), channel.getVideoNumber());
            assertEquals(youtubeChannel.getStatistics().getViewCount().intValue(), channel.getViewNumber());
            assertEquals(youtubeChannel.getStatistics().getSubscriberCount().intValue(), channel.getSubscriberNumber());
            assertEquals(youtubeChannel.getContentOwnerDetails().getContentOwner(), channel.getOwner());
        }
    }


    public static void assertVideoNotEmpty(Video video) {
        assertNotNull(video.getHashtag());
        assertNotNull(video.getUri());
        assertFalse(video.getTitle().isEmpty());
        assertFalse(video.getId().toString().isEmpty());
        assertChannelNotEmpty(video.getChannel());
    }


    public static void assertListOfVideosEqual(List<com.google.api.services.youtube.model.Video> youTubeList, List<Video> actualVideoList) {
        assertEquals(youTubeList.size(), actualVideoList.size());
        for (int i = 0; i < actualVideoList.size(); i++) {
            com.google.api.services.youtube.model.Video youtubeVideo = youTubeList.get(i);
            Video video = actualVideoList.get(i);
            assertChannelNotEmpty(video.getChannel());
            assertEquals(youtubeVideo.getSnippet().getTitle(), video.getTitle());
            assertEquals(youtubeVideo.getContentDetails().getDuration(), video.getDuration().toString());
            assertEquals(youtubeVideo.getSnippet().getTags(), video.getHashtag());
            assertEquals(youtubeVideo.getSnippet().getChannelId(), video.getChannel().getYouTubeChannelId());
        }
    }


    public static void assertChannelNotEmpty(Channel channel) {
        assertFalse(channel.getYouTubeChannelId().isEmpty());
        assertFalse(channel.getTitle().isEmpty());
        assertNotNull(channel.getSubscriberNumber());
        assertNotNull(channel.getViewNumber());
        assertNotNull(channel.getVideoNumber());
        assertNotNull(channel.getId());
    }


    public static void assertVideoDtoNotEmpty(VideoDto videoDto) {
        assertNotNull(videoDto.getId());
        assertNotNull(videoDto.getDuration());
        assertNotNull(videoDto.getPublishedAt());
        assertFalse(videoDto.getTitle().isEmpty());
        assertFalse(videoDto.getChannelTitle().isEmpty());
    }


    public static void assertChannelDtoNotEmpty(ChannelDto channelDto) {
        assertNotNull(channelDto.getId());
        assertNotNull(channelDto.getPublishedAt());
        assertNotNull(channelDto.getSubscriberNumber());
        assertNotNull(channelDto.getVideoNumber());
        assertNotNull(channelDto.getViewNumber());
        assertFalse(channelDto.getTitle().isEmpty());
    }

}
