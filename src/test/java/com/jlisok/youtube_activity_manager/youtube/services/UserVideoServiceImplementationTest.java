
package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.testutils.YouTubeApiUtils.createRandomListOfVideos;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserVideoServiceImplementationTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserVideoRepository userVideoRepository;

    @Autowired
    private UserUtils utils;

    @Autowired
    private UserVideoServiceImplementation service;

    private User user;
    private UUID id;
    private final Rating rating = Rating.LIKE;

    @BeforeEach
    void createBoundaryConditions() {
        id = UUID.randomUUID();
        String dummyToken = "jhgfbdsxnjhgbfvcdsxnjhgbfdcsx";
        user = utils.createUser(id, dummyToken, dummyToken);
    }

    @Test
    void insertVideos_whenUserDoesNotExistInDatabase() {
        //given
        List<Video> videoList = createRandomListOfVideos(1);

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        //when //then
        assertThrows(ExpectedDataNotFoundInDatabase.class, () -> service.insertListInDatabase(videoList, rating, id));
    }


    @Test
    void insertVideos_whenListOfVideosEmpty() throws ExpectedDataNotFoundInDatabase {
        //given
        List<Video> videoList = createRandomListOfVideos(0);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        //when
        service.insertListInDatabase(videoList, rating, id);

        //then
        verify(userVideoRepository).saveAll(ArgumentMatchers.anyList());
    }


    @Test
    void insertVideos_whenListOfVideosValid() throws ExpectedDataNotFoundInDatabase {
        //given
        List<Video> videoList = createRandomListOfVideos(5);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        //when
        service.insertListInDatabase(videoList, rating, id);

        //then
        verify(userVideoRepository).saveAll(ArgumentMatchers.anyList());
    }
}