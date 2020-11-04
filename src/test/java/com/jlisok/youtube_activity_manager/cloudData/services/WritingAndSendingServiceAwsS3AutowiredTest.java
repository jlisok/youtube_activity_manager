package com.jlisok.youtube_activity_manager.cloudData.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.cloudData.utils.KeyNameCreator;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserTestUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled("Real communication with AWS S3 needs to be disabled not to clutter free tier.")
class WritingAndSendingServiceAwsS3AutowiredTest implements TestProfile {

    @Autowired
    private AmazonS3 client;

    @Autowired
    private UserTestUtils userTestUtils;

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
        user = userTestUtils.createUser(userTestUtils.createRandomEmail(), userTestUtils.createRandomPassword());
        videos = VideoUtils.createRandomListOfVideos(10);
        info = new AwsObjectInfo(bucketName, keyName);
    }


    @AfterEach
    void removeObjectFromAws() {
        if (client.doesObjectExist(info.getBucketName(), info.getKeyName())) {
            client.deleteObject(new DeleteObjectRequest(info.getBucketName(), keyName));
        }
    }


    @Test
    void writeAndSendData_whenInputDataEmpty() {
        //given
        List<Video> emptyList = Lists.emptyList();

        // when
        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        service.writeAndSendData(emptyList, user.getId());

        //then
        Assertions.assertFalse(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    @Test
    void writeAndSendData_whenDataValidNotEmpty() {
        //given // when
        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        service.writeAndSendData(videos, user.getId());

        //then
        Assertions.assertTrue(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }

}