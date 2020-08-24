package com.jlisok.youtube_activity_manager.users.repositories;

import com.jlisok.youtube_activity_manager.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    void deleteByEmail(String email);

}