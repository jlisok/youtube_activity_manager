package com.jlisok.youtube_activity_manager.cloudData.client;

import java.net.URI;

public class AwsObjectInfo {

    private final String bucketName;
    private final URI uri;
    private final String keyName;

    public AwsObjectInfo(String bucketName, String keyName) {
        this.bucketName = bucketName;
        this.keyName = keyName;
        uri = URI.create("s3://" + this.bucketName + "/" + this.keyName);
    }


    public URI getURI() {
        return uri;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getKeyName() {
        return keyName;
    }

}
