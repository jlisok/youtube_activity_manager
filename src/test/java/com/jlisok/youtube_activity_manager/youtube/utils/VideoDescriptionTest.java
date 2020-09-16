package com.jlisok.youtube_activity_manager.youtube.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription.toListOfUri;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoDescriptionTest {

    @ParameterizedTest
    @MethodSource("text_examples")
    void toListOfUriTest(String text, int expectedNumberOfURI) {
        //given //when
        List<String> uriList = toListOfUri(text);

        //then
        assertEquals(expectedNumberOfURI, uriList.size());
    }

    static Stream<Arguments> text_examples() {
        return Stream.of(
                Arguments.arguments("", 0),
                Arguments.arguments("www.example.com", 1),
                Arguments.arguments("here is uri: www.example.com", 1),
                Arguments.arguments("https://www.example.com/", 1),
                Arguments.arguments("https://www.example.com/ this is one uri", 1),
                Arguments.arguments("http://www.youtube.com/", 1),
                Arguments.arguments("here is another uri http://www.youtube.com/", 1),
                Arguments.arguments("www.example.com www.example.com", 2),
                Arguments.arguments("www.example.com and another one http://www.youtube.com/", 2),
                Arguments.arguments("www.example.com, how about a coma?", 1),
                Arguments.arguments("lets see some coincidental character matching like this www", 0)
        );
    }
}