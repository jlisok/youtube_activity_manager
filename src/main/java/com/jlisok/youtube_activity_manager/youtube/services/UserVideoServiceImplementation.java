package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.utils.UserFetcher;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.UserVideo;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.UserVideoRepository;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public void insertVideosVideoCategoriesAndChannels(List<Video> videos, Rating rating, UUID userId) {
        User user = userFetcher.fetchUser(userId);
        var dbUserVideos = fetchUserVideosFromDatabase(userId);
        List<UserVideo> userVideos = videos
                .stream()
                .map(video -> createUserVideo(user, video, rating, dbUserVideos))
                .collect(Collectors.toList());
        userVideoRepository.saveAll(userVideos);
    }


    private Map<UUID, UserVideo> fetchUserVideosFromDatabase(UUID userId) {
        List<UserVideo> dbUserVideos = userVideoRepository.findByUserId(userId);
        return MapCreator.toMap(dbUserVideos, uv -> uv.getVideo().getId());
    }


    private UserVideo createUserVideo(User user, Video video, Rating rating, Map<UUID, UserVideo> dbUserVideos) {
        return Optional.ofNullable(dbUserVideos.get(video.getId()))
                       .map(userVideo -> updateUserVideo(userVideo, rating))
                       .orElse(createNewUserVideo(user, video, rating));
    }


    private UserVideo updateUserVideo(UserVideo userVideo, Rating rating) {
        userVideo.setRating(rating);
        return userVideo;
    }


    private UserVideo createNewUserVideo(User user, Video video, Rating rating) {
        UUID id = UUID.randomUUID();
        return new UserVideo(id, user, video, rating);
    }
}
