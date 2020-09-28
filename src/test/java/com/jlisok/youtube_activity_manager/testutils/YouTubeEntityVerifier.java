package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YouTubeEntityVerifier {

    public static void assertResponseVideosNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("title"));
        assertTrue(response.contains("duration"));
        assertTrue(response.contains("hashtag"));
        assertTrue(response.contains("uri"));
    }


    public static void assertResponseChannelsNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("title"));
        assertTrue(response.contains("youTubeChannelId"));
        assertTrue(response.contains("viewNumber"));
        assertTrue(response.contains("subscriberNumber"));
        assertTrue(response.contains("videoNumber"));
    }


    public static void assertListOfChannelsEqual(List<com.google.api.services.youtube.model.Channel> youTubeList, List<Channel> actualChannelList) {
        assertEquals(youTubeList.size(), actualChannelList.size());
        for (int i = 0; i < actualChannelList.size(); i++) {
            assertEquals(youTubeList.get(i).getSnippet().getCountry(), actualChannelList.get(i).getCountry());
            assertEquals(youTubeList.get(i).getSnippet().getDefaultLanguage(), actualChannelList.get(i).getLanguage());
            assertEquals(youTubeList.get(i).getSnippet().getTitle(), actualChannelList.get(i).getTitle());
            assertEquals(youTubeList.get(i).getStatistics().getVideoCount().intValue(), actualChannelList.get(i)
                                                                                                         .getVideoNumber());
            assertEquals(youTubeList.get(i).getStatistics().getVideoCount().intValue(), actualChannelList.get(i)
                                                                                                         .getVideoNumber());
            assertEquals(youTubeList.get(i).getStatistics().getViewCount().intValue(), actualChannelList.get(i)
                                                                                                        .getViewNumber());
            assertEquals(youTubeList.get(i).getStatistics().getSubscriberCount().intValue(), actualChannelList.get(i)
                                                                                                              .getSubscriberNumber());
            assertEquals(youTubeList.get(i).getContentOwnerDetails().getContentOwner(), actualChannelList.get(i)
                                                                                                         .getOwner());
        }
    }


    public static void assertVideoNotEmptyOmitChannel(Video video) {
        assertNotNull(video.getHashtag());
        assertNotNull(video.getUri());
        assertFalse(video.getTitle().isEmpty());
        assertFalse(video.getId().toString().isEmpty());
    }


    public static void assertListOfVideosEqual(List<com.google.api.services.youtube.model.Video> youTubeList, List<Video> actualVideoList) {
        assertEquals(youTubeList.size(), actualVideoList.size());
        for (int i = 0; i < actualVideoList.size(); i++) {
            assertEquals(youTubeList.get(i).getSnippet().getTitle(),
                         actualVideoList.get(i).getTitle());
            assertEquals(youTubeList.get(i).getContentDetails().getDuration(),
                         actualVideoList.get(i).getDuration().toString());
            assertEquals(youTubeList.get(i).getSnippet().getTags(),
                         actualVideoList.get(i).getHashtag());
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
}
