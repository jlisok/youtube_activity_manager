package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.services.youtube.model.VideoCategory;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.ChannelDto;
import com.jlisok.youtube_activity_manager.youtube.dto.VideoDto;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

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
        IntStream.range(0, actualChannelList.size())
                 .forEach(i -> {
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
                 });
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
        IntStream.range(0, actualVideoList.size())
                 .forEach(i -> {
                     com.google.api.services.youtube.model.Video youtubeVideo = youTubeList.get(i);
                     Video video = actualVideoList.get(i);
                     assertChannelNotEmpty(video.getChannel());
                     assertEquals(youtubeVideo.getSnippet().getTitle(), video.getTitle());
                     assertEquals(youtubeVideo.getContentDetails().getDuration(), video.getDuration().toString());
                     assertEquals(youtubeVideo.getSnippet().getTags(), video.getHashtag());
                     assertEquals(youtubeVideo.getSnippet().getChannelId(), video.getChannel().getYouTubeChannelId());
                     assertEquals(youtubeVideo.getSnippet().getCategoryId(), video.getVideoCategory().getYoutubeId());
                 });
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
        assertFalse(videoDto.getVideoCategory().isEmpty());
    }


    public static void assertChannelDtoNotEmpty(ChannelDto channelDto) {
        assertNotNull(channelDto.getId());
        assertNotNull(channelDto.getPublishedAt());
        assertNotNull(channelDto.getSubscriberNumber());
        assertNotNull(channelDto.getVideoNumber());
        assertNotNull(channelDto.getViewNumber());
        assertFalse(channelDto.getTitle().isEmpty());
    }


    public static void assertYouTubeVideoCategoryAndVideoCategoryEqual(List<VideoCategory> youTubeCategories, List<com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory> videoCategories) {
        assertEquals(youTubeCategories.size(), videoCategories.size());
        IntStream.range(0, videoCategories.size())
                 .forEach(i -> {
                     var ytCategory = youTubeCategories.get(i);
                     var category = videoCategories.get(i);
                     Assertions.assertEquals(ytCategory.getId(), category.getYoutubeId());
                     Assertions.assertEquals(ytCategory.getSnippet().getTitle(), category.getCategoryName());
                 });
    }

    public static void assertDbIdsAndInputIdsExistsAndEqual(List<com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory> returnedVideoCategories, List<com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory> dbVideoCategories, List<String> dbIds, List<String> ids) {
        Map<String, com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory> videoCategoriesMap = MapCreator
                .toMap(returnedVideoCategories, com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory::getYoutubeId, Function
                        .identity());

        IntStream.range(0, dbIds.size())
                 .forEach(i -> {
                     assertTrue(videoCategoriesMap.containsKey(dbIds.get(i)));
                     var actual = videoCategoriesMap.get(dbIds.get(i));
                     assertEquals(dbVideoCategories.get(i), actual);
                 });

        IntStream.range(0, ids.size())
                 .forEach(i -> assertTrue(videoCategoriesMap.containsKey(ids.get(i))));
    }
}
