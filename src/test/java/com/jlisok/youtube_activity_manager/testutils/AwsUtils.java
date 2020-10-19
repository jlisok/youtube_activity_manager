package com.jlisok.youtube_activity_manager.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.cloudData.dto.CloudDataDto;
import com.jlisok.youtube_activity_manager.cloudData.dto.YouTubeActivityCloudData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AwsUtils {

    @Autowired
    private ObjectMapper mapper;

    public String createDummyJsonToSend(int size) throws JsonProcessingException {
        List<YouTubeActivityCloudData> ytActivity = IntStream
                .range(0, size)
                .mapToObj(i -> new YouTubeActivityCloudData(createUris(i + 1), createHashTags(i + 1)))
                .collect(Collectors.toList());

        List<String> videoCategories = IntStream
                .range(0, size)
                .mapToObj(i -> VideoUtils.createRandomString())
                .collect(Collectors.toList());

        var dataDto = new CloudDataDto(ytActivity, videoCategories);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataDto);
    }


    private List<String> createUris(int size) {
        return IntStream
                .range(0, size)
                .mapToObj(i -> VideoUtils.createRandomString())
                .collect(Collectors.toList());
    }

    private List<String> createHashTags(int size) {
        return IntStream
                .range(0, size)
                .mapToObj(i -> VideoUtils.createRandomString())
                .collect(Collectors.toList());
    }
}
