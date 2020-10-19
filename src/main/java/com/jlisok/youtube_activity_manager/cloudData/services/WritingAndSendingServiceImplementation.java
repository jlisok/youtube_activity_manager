package com.jlisok.youtube_activity_manager.cloudData.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsInfo;
import com.jlisok.youtube_activity_manager.cloudData.utils.KeyNameCreator;
import com.jlisok.youtube_activity_manager.cloudData.writers.ContentSender;
import com.jlisok.youtube_activity_manager.cloudData.writers.ContentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WritingAndSendingServiceImplementation<T> implements WritingAndSendingService<T> {

    @Value("${aws.bucket_name}")
    private String bucketName;
    private final ContentWriter<T> contentWriter;
    private final KeyNameCreator keyNameCreator;
    private final ContentSender contentSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public WritingAndSendingServiceImplementation(ContentWriter<T> contentWriter, KeyNameCreator keyNameCreator, ContentSender contentSender) {
        this.keyNameCreator = keyNameCreator;
        logger.info("WritingAndSendingService - initialization");
        this.contentWriter = contentWriter;
        this.contentSender = contentSender;
    }

    @Override
    public void writeAndSendData(T data) throws JsonProcessingException {
        String contentToSend = contentWriter.writeContent(data);
        String fileName = keyNameCreator.createKeyName();
        logger.info("WritingAndSendingService fileName: {} - content created", fileName);
        AwsInfo awsInfo = new AwsInfo(bucketName, fileName);
        contentSender.sendContent(contentToSend, awsInfo);
        logger.info("WritingAndSendingService URI: {} - content sent", awsInfo.getURI());
    }
}
