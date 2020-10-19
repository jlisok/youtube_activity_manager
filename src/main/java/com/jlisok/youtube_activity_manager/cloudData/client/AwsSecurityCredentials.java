package com.jlisok.youtube_activity_manager.cloudData.client;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.jlisok.youtube_activity_manager.cloudData.exceptions.AwsSecurityCredentialsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AwsSecurityCredentials {

    private final String accessKeyId;
    private final String secretAccessKey;


    public AwsSecurityCredentials(Path path) {
        List<String> securityInfo = getAccessKeys(path);
        accessKeyId = securityInfo.get(0);
        secretAccessKey = securityInfo.get(1);
    }


    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(accessKeyId, secretAccessKey);
    }


    private List<String> getAccessKeys(Path path) throws AwsSecurityCredentialsException {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new AwsSecurityCredentialsException("Trouble while acquiring security credentials.", e);
        }
    }

}
