package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
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

    private final UserRepository userRepository;
    private final UserVideoRepository userVideoRepository;

    @Autowired
    public UserVideoServiceImplementation(UserRepository userRepository, UserVideoRepository userVideoRepository) {
        this.userRepository = userRepository;
        this.userVideoRepository = userVideoRepository;
    }

    @Override
    @Transactional
    public void insertListInDatabase(List<Video> list, Rating rating, UUID userId) throws ExpectedDataNotFoundInDatabase {
        User user = fetchUser(userId);
        List<UserVideo> userVideoList = list
                .stream()
                .map(video -> createUserVideo(user, video, rating))
                .collect(Collectors.toList());
        userVideoRepository.saveAll(userVideoList);
    }


    private User fetchUser(UUID userId) throws ExpectedDataNotFoundInDatabase {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ExpectedDataNotFoundInDatabase("Error while fetching User from database. User userId: " + userId + "not found."));
    }


    private UserVideo createUserVideo(User user, Video video, Rating rating) {
        UUID id = UUID.randomUUID();
        return new UserVideo(id, user, video, rating);
    }
}
