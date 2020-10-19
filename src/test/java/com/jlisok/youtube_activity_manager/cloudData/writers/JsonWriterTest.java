package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class JsonWriterTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private JsonWriter writer;

    private List<Video> videos;

    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        User user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        videos = VideoUtils.createRandomListOfVideos(10, user);
    }


    @Test
    void writeContent() throws JsonProcessingException {
        //given //when
        String jsonToWrite = writer.writeContent(videos);

        //then
        videos.forEach(video -> {
            video.getUri()
                 .forEach(uri -> Assertions.assertTrue(jsonToWrite.contains(uri)));

            video.getHashtag()
                 .forEach(tag -> Assertions.assertTrue(jsonToWrite.contains(tag)));

        });
    }


    @Test
    void writeContent_whenVideosEmpty() throws JsonProcessingException {
        //given
        videos = Lists.emptyList();

        // when
        String jsonToWrite = writer.writeContent(videos);

        //then
        Assertions.assertTrue(jsonToWrite.isEmpty());
    }
}