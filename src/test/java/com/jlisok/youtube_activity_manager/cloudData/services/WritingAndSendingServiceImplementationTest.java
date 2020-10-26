package com.jlisok.youtube_activity_manager.cloudData.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.cloudData.utils.KeyNameCreator;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class WritingAndSendingServiceImplementationTest implements TestProfile {

    @MockBean
    private AmazonS3 client;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private WritingAndSendingService<List<Video>> service;

    @MockBean
    private KeyNameCreator keyNameCreator;

    private final String keyName = "content.json";

    @Value("${aws.s3.bucket_name}")
    private String bucketName;
    private AwsObjectInfo info;
    private List<Video> videos;
    private User user;


    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.createUser(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        videos = VideoUtils.createRandomListOfVideos(10);
        info = new AwsObjectInfo(bucketName, keyName);
    }


    @Test
    void writeAndSendData_whenInputDataEmpty() {
        //given
        List<Video> emptyList = Lists.emptyList();

        // when
        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        when(client.putObject(eq(info.getBucketName()), eq(info.getKeyName()), any(String.class)))
                .thenReturn(new PutObjectResult());

        service.writeAndSendData(emptyList, user.getId());

        //then
        verify(client, never()).putObject(eq(info.getBucketName()), eq(info.getKeyName()), any(String.class));
    }


    @Test
    void writeAndSendData_whenDataValidNotEmpty() {
        //given // when
        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        when(client.putObject(eq(info.getBucketName()), eq(info.getKeyName()), any(String.class)))
                .thenReturn(new PutObjectResult());

        service.writeAndSendData(videos, user.getId());

        //then
        verify(client).putObject(eq(info.getBucketName()), eq(info.getKeyName()), any(String.class));
    }

}