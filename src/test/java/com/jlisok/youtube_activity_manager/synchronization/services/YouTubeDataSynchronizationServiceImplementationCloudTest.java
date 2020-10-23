package com.jlisok.youtube_activity_manager.synchronization.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.channels.repositories.ChannelRepository;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.cloudData.utils.KeyNameCreator;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.ChannelAndSubscriptionUtils;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.testutils.VideoUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videoCategories.repositories.VideoCategoryRepository;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.videos.repositories.VideoRepository;
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
class YouTubeDataSynchronizationServiceImplementationCloudTest implements TestProfile {

    @Autowired
    private AmazonS3 client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserVideoRepository userVideoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private VideoCategoryRepository videoCategoryRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private YouTubeDataSynchronizationServiceImplementation service;

    @MockBean
    private KeyNameCreator keyNameCreator;

    private final String keyName = "content.json";

    @Value("${aws.s3.bucket_name}")
    private String bucketName;
    private AwsObjectInfo info;
    private User user;
    private List<UserVideo> userVideos;
    private List<Channel> channels;
    private List<VideoCategory> videoCategories;
    private List<Video> videos;


    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userUtils.createUserWithDataFromToken(userUtils.createRandomEmail(), userUtils.createRandomPassword());
        info = new AwsObjectInfo(bucketName, keyName);
    }


    /**
     * Async methods do not work with @transactional. Must remove all added entities from database manually
     */
    @AfterEach
    void removeObjectFromAws() {
        if (client.doesObjectExist(info.getBucketName(), info.getKeyName())) {
            client.deleteObject(new DeleteObjectRequest(info.getBucketName(), keyName));
        }
        if (userVideos != null) {
            videoCategoryRepository.deleteAll(videoCategories);
            channelRepository.deleteAll(channels);
            videoRepository.deleteAll(videos);
            userVideoRepository.deleteAll(userVideos);
            userRepository.delete(user);
        }
    }


    @Test
    void writeAndSendData_whenInputDataEmpty() {
        //given // when
        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        service.sendDataToCloud(user.getId()).join();

        //then
        Assertions.assertFalse(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    @Test
    void writeAndSendData_whenDataValidNotEmpty() {
        //given // when
        insertUserAndUsersVideosInDatabase(user);

        when(keyNameCreator.createKeyName(user.getId()))
                .thenReturn(keyName);

        service.sendDataToCloud(user.getId()).join();

        //then
        Assertions.assertTrue(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    private void insertUserAndUsersVideosInDatabase(User user) {
        userRepository.save(user);
        int size = 10;
        channels = ChannelAndSubscriptionUtils.createRandomListOfChannels(size);
        videoCategories = VideoUtils.createRandomListOfVideoCategories(size);
        videos = VideoUtils.createVideos(size, channels, videoCategories);
        userVideos = VideoUtils.createListOfUserVideos(videos, user, Rating.LIKE);
        userVideoRepository.saveAll(userVideos);
        userVideoRepository.flush();
    }


}