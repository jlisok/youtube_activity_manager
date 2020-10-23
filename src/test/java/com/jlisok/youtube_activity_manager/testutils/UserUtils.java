package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.utils.DtoToUserTranslator;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalDataBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.models.UserBuilder;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class UserUtils implements TestProfile {

    private final DtoToUserTranslator dtoToUserTranslator;
    private final UserRepository repository;

    @Autowired
    public UserUtils(DtoToUserTranslator dtoToUserTranslator, UserRepository userRepository) {
        this.dtoToUserTranslator = dtoToUserTranslator;
        this.repository = userRepository;
    }


    public void assertUsersAreEqualWhenIgnoringPassword(User expected, User actual) {
        expected.setPassword("");
        actual.setPassword("");
        Assertions.assertEquals(expected, actual);
    }


    @Transactional
    public void insertUserInDatabase(RegistrationRequestDto dto) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = dtoToUserTranslator.translate(dto, now, id);
        repository.saveAndFlush(user);

        assertTrue(repository.existsByEmail(user.getEmail()));
    }


    @Transactional
    public User insertUserInDatabase(String userEmail, String userPassword) throws RegistrationException {
        User user = createUserWithDataFromToken(userEmail, userPassword);
        repository.saveAndFlush(user);

        assertTrue(repository.existsByEmail(user.getEmail()));
        return user;
    }


    @Transactional
    public User insertUserFromTokenInDatabase(String token, GoogleIdToken googleIdToken, String accessToken) {
        User user = createUserWithDataFromToken(token, googleIdToken, accessToken);
        repository.saveAndFlush(user);
        assertTrue(repository.existsByEmail(user.getEmail()));
        return user;
    }


    public User createUserWithDataFromToken(String userEmail, String userPassword) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        RegistrationRequestDto dto = RandomRegistrationDto.createValidRegistrationDto(userEmail, userPassword);
        return dtoToUserTranslator.translate(dto, now, id);
    }


    public User createUserWithDataFromToken(String token, GoogleIdToken googleIdToken, String accessToken) {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        GoogleIdToken.Payload userData = googleIdToken.getPayload();
        String fistName = userData
                .get("given_name")
                .toString();
        UserPersonalData userPersonalData = new UserPersonalDataBuilder()
                .setId(userId)
                .setFirstName(fistName)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createUserPersonalData();
        return new UserBuilder()
                .setId(userId)
                .setEmail(createRandomEmail())
                .setGoogleId(createRandomGoogleId())
                .setGoogleIdToken(token)
                .setAccessToken(accessToken)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUserPersonalData(userPersonalData)
                .createUser();
    }


    public User createUserWithDataFromToken(UUID userId, String googleIdToken, String accessToken) {
        Instant now = Instant.now();
        String googleId = UUID.randomUUID().toString();
        UserPersonalData userPersonalData = new UserPersonalDataBuilder()
                .setId(userId)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createUserPersonalData();
        return new UserBuilder()
                .setId(userId)
                .setEmail(createRandomEmail())
                .setGoogleId(googleId)
                .setGoogleIdToken(googleIdToken)
                .setAccessToken(accessToken)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUserPersonalData(userPersonalData)
                .createUser();
    }

    @Transactional
    public void insertUserInDatabaseSameGoogleIdDifferentEmail(String token, GoogleIdToken googleIdToken, String accessToken) {
        User user = createUserWithDataFromToken(token, googleIdToken, accessToken);
        user.setEmail(createRandomEmail());
        user.setGoogleId(googleIdToken.getPayload().getSubject());
        repository.saveAndFlush(user);

        assertTrue(repository.existsByEmail(user.getEmail()));
    }


    public String createRandomEmail() {
        return RandomStringUtils.randomAlphanumeric(20) + "@gmail.com";
    }


    public String createRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public String createRandomGoogleId() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
