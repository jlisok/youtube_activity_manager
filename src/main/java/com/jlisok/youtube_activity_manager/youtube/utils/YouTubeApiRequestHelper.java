package com.jlisok.youtube_activity_manager.youtube.utils;

import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.jlisok.youtube_activity_manager.youtube.constants.YouTubeApiClientRequest.MAX_ALLOWED_RESULTS_PER_PAGE;

public class YouTubeApiRequestHelper {

    public static List<String> separateIdsIntoMaxRequestCapacity(List<String> channelIds) {
        List<List<String>> channelIdSubLists = ListUtils.partition(channelIds, MAX_ALLOWED_RESULTS_PER_PAGE);
        return channelIdSubLists
                .stream()
                .map(ids -> String.join(",", ids))
                .collect(Collectors.toList());
    }
}
