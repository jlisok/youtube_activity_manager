package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.algorithms.Algorithm;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm jwtAlgorithm;
    private final TokenCreator tokenCreator;
    private final User dummyUser;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, Algorithm jwtAlgorithm, TokenCreator tokenCreator) {
        logger.debug("Login Service - initialization.");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAlgorithm = jwtAlgorithm;
        this.tokenCreator = tokenCreator;
        this.dummyUser = new User();
        this.dummyUser.setPassword("");
    }

    public String authenticateUser(LoginRequestDto loginRequestDto) throws FailedLoginException {

        User user = userRepository
                    .findByEmail(loginRequestDto.getEmail())
                    .orElse(dummyUser);

        logger.debug("Login Service - fetching user from database - success.");

        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            Instant now = Instant.now();
            String token = tokenCreator.create(user.getId().toString(), jwtAlgorithm, now);
            logger.info("Login Service - success.");
            return token;
        } else {
            logger.info("Login Service - failed to authenticate user id {}. Password or login does not match.", user.getId());
            throw new FailedLoginException("User id " + user.getId() + " login failed. Password or login does not match.");
        }

    }

}
