package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.time.Instant;
import java.util.UUID;

@Service
public class TraditionalLoginServiceImplementation implements TraditionalLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenCreator tokenCreator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserLoginDetails dummyUser = new UserLoginDetails(UUID.randomUUID(), "");


    @Autowired
    public TraditionalLoginServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenCreator tokenCreator) {
        logger.debug("Login Service - initialization.");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenCreator = tokenCreator;
    }


    @Override
    public String authenticateUser(LoginRequestDto loginRequestDto) throws FailedLoginException {
        UserLoginDetails userLoginDetails = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .map(u -> new UserLoginDetails(u.getId(), u.getPassword()))
                .orElse(dummyUser);

        logger.debug("Login Service - fetching user: " + loginRequestDto.getEmail() + " from database - success.");
        return createTokenIfAuthorized(loginRequestDto, userLoginDetails.getId(), userLoginDetails.getPassword());
    }


    private String createTokenIfAuthorized(LoginRequestDto dto, UUID userId, String userPassword) throws FailedLoginException {
        if (passwordEncoder.matches(dto.getPassword(), userPassword)) {
            Instant now = Instant.now();
            String token = tokenCreator.create(userId.toString(), now);
            logger.info("Login Service - success.");
            return token;
        } else {
            throw new FailedLoginException("User: " + dto.getEmail() + " login failed. Password or login does not match.");
        }
    }


    private static class UserLoginDetails {

        private final UUID id;
        private final String password;

        UserLoginDetails(UUID id, String password) {
            this.id = id;
            this.password = password;
        }

        UUID getId() {
            return id;
        }

        String getPassword() {
            return password;
        }
    }

}
