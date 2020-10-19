package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.jlisok.youtube_activity_manager.cloudData.client.AwsInfo;

public interface ContentSender {

    void sendContent(String json, AwsInfo awsInfo);
}
