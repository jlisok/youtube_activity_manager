package com.jlisok.youtube_activity_manager.security.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3 {

    private final Regions region = Regions.EU_CENTRAL_1;

    @Value("${aws.s3.access_key_id}")
    private String accessKeyId;

    @Value("${aws.s3.secret_access_key_id}")
    private String secretAccessKeyId;

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
        return new AWSStaticCredentialsProvider(basicAwsCredentials());
    }

    @Bean
    public BasicAWSCredentials basicAwsCredentials() {
        return new BasicAWSCredentials(accessKeyId, secretAccessKeyId);
    }
}
