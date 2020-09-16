package com.jlisok.youtube_activity_manager.youtube.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDescription {

    private static final String URI_REGEX = "(?:^|[\\W])((ht|f)tp(s?)://|www\\.)"
            + "(([\\w\\-]+\\.)+?([\\w\\-.~]+/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)";
    private static final Pattern pattern = Pattern.compile(URI_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


    public static List<String> toListOfUri(String text) {
        List<String> containedUrls = new ArrayList<>();
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            String uriToAdd = text.substring(urlMatcher.start(1), urlMatcher.end());
            containedUrls.add(uriToAdd);
        }
        return containedUrls;
    }
}
