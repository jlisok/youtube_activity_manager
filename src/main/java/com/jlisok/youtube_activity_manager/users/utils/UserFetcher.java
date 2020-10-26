package com.jlisok.youtube_activity_manager.users.utils;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFetcher {

    private final UserRepository repository;

    @Autowired
    public UserFetcher(UserRepository repository) {
        this.repository = repository;
    }


    public User fetchUser(UUID userId) throws ExpectedDataNotFoundInDatabase {
        return repository
                .findById(userId)
                .orElseThrow(() -> new ExpectedDataNotFoundInDatabase("Error while fetching User from database. Expected user userId: " + userId + " not found."));
    }
}
