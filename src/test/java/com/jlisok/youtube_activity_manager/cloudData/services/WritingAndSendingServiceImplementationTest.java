package com.jlisok.youtube_activity_manager.cloudData.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.cloudData.utils.KeyNameCreator;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class WritingAndSendingServiceImplementationTest implements TestProfile {

    @Autowired
    private AmazonS3 client;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private WritingAndSendingService<List<Video>> service;

    @MockBean
    private KeyNameCreator keyNameCreator;

    private final String keyName = "content.json";

    @Value("${aws.s3.test_bucket_name}")
    private String bucketName;
    private AwsObjectInfo info;
    private List<Video> videos;


    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        User user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        videos = VideoUtils.createRandomListOfVideos(10, user);
        info = new AwsObjectInfo(bucketName, keyName);
    }


    @AfterEach
    void removeObjectFromAws() {
        if (client.doesObjectExist(info.getBucketName(), info.getKeyName())) {
            client.deleteObject(new DeleteObjectRequest(info.getBucketName(), keyName));
        }
    }


    @Test
    void writeAndSendData_whenInputDataEmpty() throws JsonProcessingException {
        //given
        List<Video> emptyList = Lists.emptyList();

        // when
        when(keyNameCreator.createKeyName())
                .thenReturn(keyName);

        service.writeAndSendData(emptyList);

        //then
        Assertions.assertFalse(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    @Test
    void writeAndSendData_whenDataValidNotEmpty() throws JsonProcessingException {
        //given // when
        when(keyNameCreator.createKeyName())
                .thenReturn(keyName);

        service.writeAndSendData(videos);

        //then
        Assertions.assertTrue(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }

}