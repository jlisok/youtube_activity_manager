package com.jlisok.youtube_activity_manager.security.configs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jlisok.youtube_activity_manager.cloudData.client.AwsSecurityCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AwsS3 {

    private final Regions region = Regions.EU_CENTRAL_1;

    @Value("${aws.client_secrets}")
    private String clientSecrets;


    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(awsStaticCredentialsProvider())
                .withRegion(region)
                .build();
    }

    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        return new AWSStaticCredentialsProvider(awsCredentials());
    }

    @Bean
    public AWSCredentials awsCredentials() {
        Path pathToClientSecrets = Paths.get(clientSecrets);
        AwsSecurityCredentials awsSecurityCredentials = new AwsSecurityCredentials(pathToClientSecrets);
        return awsSecurityCredentials.getCredentials();
    }
}
