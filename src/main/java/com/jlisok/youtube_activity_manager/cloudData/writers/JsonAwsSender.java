package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;
import com.jlisok.youtube_activity_manager.cloudData.exceptions.AwsContentWritingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonAwsSender implements ContentSender {

    private final AmazonS3 client;

    @Autowired
    public JsonAwsSender(AmazonS3 client) {
        this.client = client;
    }

    @Override
    public void sendContent(String jsonToSend, AwsObjectInfo awsObjectInfo) {
        if (jsonToSend.isEmpty()){
            return;
        }
        try {
            client.putObject(awsObjectInfo.getBucketName(), awsObjectInfo.getKeyName(), jsonToSend);
        } catch (AmazonClientException e) {
            throw new AwsContentWritingException("Trouble while writing content " + jsonToSend + "to AWS server", e);
        }
    }
}
