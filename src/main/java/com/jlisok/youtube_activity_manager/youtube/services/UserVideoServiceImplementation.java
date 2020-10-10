package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.utils.UserFetcher;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserVideoServiceImplementation implements UserVideoService {

    private final UserVideoRepository userVideoRepository;
    private final UserFetcher userFetcher;

    @Autowired
    public UserVideoServiceImplementation(UserVideoRepository userVideoRepository, UserFetcher userFetcher) {
        this.userVideoRepository = userVideoRepository;
        this.userFetcher = userFetcher;
    }

    @Override
    @Transactional
    public void insertVideosInDatabase(List<Video> list, Rating rating, UUID userId) throws ExpectedDataNotFoundInDatabase {
        User user = userFetcher.fetchUser(userId);
        List<UserVideo> userVideoList = list
                .stream()
                .map(video -> createUserVideo(user, video, rating))
                .collect(Collectors.toList());
        userVideoRepository.saveAll(userVideoList);
    }


    private UserVideo createUserVideo(User user, Video video, Rating rating) {
        UUID id = UUID.randomUUID();
        return new UserVideo(id, user, video, rating);
    }
}
