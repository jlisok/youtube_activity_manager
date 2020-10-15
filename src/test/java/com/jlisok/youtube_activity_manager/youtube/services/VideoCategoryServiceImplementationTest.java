package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.testutils.YouTubeEntityVerifier;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videoCategories.repositories.VideoCategoryRepository;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeClient;
import com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest;
import org.apache.commons.collections4.ListUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

@SpringBootTest
class VideoCategoryServiceImplementationTest {

    @Autowired
    private VideoCategoryService service;


    @MockBean
    private VideoCategoryRepository repository;

    @MockBean
    private YouTubeClient client;

    private final String dummyAccessToken = "dummyaccesstokendummyaccesstokendummyaccesstokendummyaccesstoken";

    private List<String> ids;
    private List<String> dbIds;
    private List<com.google.api.services.youtube.model.VideoCategory> youTubeCategories;
    private List<VideoCategory> dbCategories;

    @BeforeEach
    void createInitialConditions() {
        ids = IntStream
                .range(0, 5)
                .mapToObj(i -> VideoUtils.createRandomString())
                .collect(Collectors.toList());

        dbIds = ListUtils.partition(ids, 2).get(0);

        youTubeCategories = VideoUtils.createRandomListOfYouTubeVideoCategoriesById(ids);
        dbCategories = VideoUtils.createListOfVideoCategoriesGivenYTIds(dbIds);
    }


    @Test
    void getVideoCategories_whenNothingInDatabase() {
        //given //when
        when(client.fetchVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids))
                .thenReturn(youTubeCategories);

        when(repository.findAllByYoutubeIdIn(ids))
                .thenReturn(Lists.emptyList());

        List<VideoCategory> videoCategories = service.getVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids);

        //then
        Assertions.assertNotNull(videoCategories);
        Assertions.assertFalse(videoCategories.isEmpty());
        YouTubeEntityVerifier.assertYouTubeVideoCategoryAndVideoCategoryEqual(youTubeCategories, videoCategories);
    }


    @Test
    void getVideoCategories_whenEntitiesInDatabase() {
        //given //when
        when(client.fetchVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids))
                .thenReturn(youTubeCategories);

        when(repository.findAllByYoutubeIdIn(ids))
                .thenReturn(dbCategories);

        List<VideoCategory> videoCategories = service.getVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids);

        //then
        Assertions.assertNotNull(videoCategories);
        Assertions.assertFalse(videoCategories.isEmpty());
        Assertions.assertEquals(ids.size(), videoCategories.size());
        YouTubeEntityVerifier.assertDbIdsAndInputIdsExistsAndEqual(videoCategories, dbCategories, dbIds, ids);
    }


    @Test
    void getVideoCategories_whenNoSuchCategoryApi() {
        //given //when
        when(client.fetchVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids))
                .thenReturn(Lists.emptyList());

        when(repository.findAllByYoutubeIdIn(ids))
                .thenReturn(Lists.emptyList());

        List<VideoCategory> videoCategories = service.getVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids);

        //then
        Assertions.assertNotNull(videoCategories);
        Assertions.assertTrue(videoCategories.isEmpty());
    }


    @Test
    void getVideoCategories_whenIdsEmpty() {
        //given //when
        List<String> ids = Collections.emptyList();

        when(client.fetchVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids))
                .thenReturn(Lists.emptyList());

        when(repository.findAllByYoutubeIdIn(ids))
                .thenReturn(Lists.emptyList());

        List<VideoCategory> videoCategories = service.getVideoCategories(dummyAccessToken, YouTubeApiClientRequest.VIDEO_CATEGORY_REQUEST_PARTS, ids);

        //then
        Assertions.assertNotNull(videoCategories);
        Assertions.assertTrue(videoCategories.isEmpty());
    }

}