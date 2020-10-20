package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.cloudData.dto.CloudDataDto;
import com.jlisok.youtube_activity_manager.cloudData.dto.YouTubeActivityCloudData;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideosJsonWriter implements ContentWriter<List<Video>> {

    private final ObjectMapper mapper;

    @Autowired
    public VideosJsonWriter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String writeContent(List<Video> videos) throws JsonProcessingException {
        if (videos.isEmpty()) {
            return Strings.EMPTY;
        }
        List<YouTubeActivityCloudData> youTubeActivityCloudData = videos
                .stream()
                .map(video -> new YouTubeActivityCloudData(video.getUri(), video.getHashtag()))
                .collect(Collectors.toList());
        List<String> videoCategories = videos
                .stream()
                .map(video -> video.getVideoCategory().getCategoryName())
                .distinct()
                .collect(Collectors.toList());
        CloudDataDto dataDto = new CloudDataDto(youTubeActivityCloudData, videoCategories);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataDto);
    }
}
