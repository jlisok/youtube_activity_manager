package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.testutils.AwsUtils;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonAwsSenderTest implements TestProfile {

    @Autowired
    private AmazonS3 client;

    @Autowired
    private JsonAwsSender sender;

    @Autowired
    private AwsUtils awsUtils;


    private final String jsonName = "content.json";

    @Value("${aws.s3.bucket_name}")
    private String bucketName;
    private AwsObjectInfo info;
    private String jsonToSend;

    @BeforeEach
    void prepareInitialConditions() throws JsonProcessingException {
        info = new AwsObjectInfo(bucketName, jsonName);
        jsonToSend = awsUtils.createDummyJsonToSend(10);
    }

    @AfterEach
    void removeObjectFromAws() {
        if (client.doesObjectExist(info.getBucketName(), info.getKeyName())) {
            client.deleteObject(new DeleteObjectRequest(info.getBucketName(), jsonName));
        }
    }

    @Test
    void sendContent_whenContentEmpty() {
        //given
        jsonToSend = Strings.EMPTY;

        //when
        sender.sendContent(jsonToSend, info);

        //then
        Assertions.assertFalse(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    @Test
    void sendContent_whenContentNotEmpty() {
        //given //when
        sender.sendContent(jsonToSend, info);

        //then
        Assertions.assertTrue(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
    }


    @Test
    void sendContent_whenUpdatingExistingFile() {
        //given //when
        sender.sendContent(jsonToSend, info);
        var cloudObjectsBefore = client.listObjects(info.getBucketName()).getObjectSummaries();
        sender.sendContent(jsonToSend, info);

        //then
        Assertions.assertTrue(client.doesObjectExist(info.getBucketName(), info.getKeyName()));
        var cloudObjectsAfter = client.listObjects(info.getBucketName()).getObjectSummaries();
        Assertions.assertEquals(cloudObjectsBefore.size(), cloudObjectsAfter.size());
    }
}