package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.jlisok.youtube_activity_manager.cloudData.client.AwsObjectInfo;

public interface ContentSender {

    void sendContent(String json, AwsObjectInfo awsObjectInfo);
}
