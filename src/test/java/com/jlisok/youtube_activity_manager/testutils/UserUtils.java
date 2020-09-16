package com.jlisok.youtube_activity_manager.testutils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
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

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class UserUtils {

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


    public void insertUserInDatabase(RegistrationRequestDto dto) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = dtoToUserTranslator.translate(dto, now, id);
        repository.saveAndFlush(user);

        assertTrue(repository
                .findByEmail(user.getEmail())
                .isPresent());

    }


    public User insertUserInDatabase(String userEmail, String userPassword) throws RegistrationException {
        User user = createUser(userEmail, userPassword);
        repository.saveAndFlush(user);

        assertTrue(repository
                .findByEmail(user.getEmail())
                .isPresent());

        return user;
    }


    public User createUser(String userEmail, String userPassword) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        RegistrationRequestDto dto = RandomRegistrationDto.createValidRegistrationDto(userEmail, userPassword);
        return dtoToUserTranslator.translate(dto, now, id);
    }


    public User createUser(String token, GoogleIdToken googleIdToken) {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        Payload userData = googleIdToken.getPayload();
        String fistName = userData
                .get("given_name")
                .toString();
        String googleId = userData.getSubject();
        UserPersonalData userPersonalData = new UserPersonalDataBuilder()
                .setId(userId)
                .setFirstName(fistName)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createUserPersonalData();
        return new UserBuilder()
                .setId(userId)
                .setEmail(userData.getEmail())
                .setGoogleId(googleId)
                .setGoogleIdToken(token)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUserPersonalData(userPersonalData)
                .createUser();
    }


    public User createUser(UUID userId, String token, String accessToken) {
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
                .setGoogleIdToken(token)
                .setAccessToken(accessToken)
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUserPersonalData(userPersonalData)
                .createUser();
    }

    public void insertUserInDatabaseSameGoogleIdDifferentEmail(String token, GoogleIdToken googleIdToken) {
        User user = createUser(token, googleIdToken);
        user.setEmail(createRandomEmail());
        repository.saveAndFlush(user);

        assertTrue(repository
                .findByEmail(user.getEmail())
                .isPresent());

    }


    public User createUserSameGoogleIdDifferentEmailAndId(User user) {
        UUID id = UUID.randomUUID();
        UserPersonalData userPersonalData = new UserPersonalDataBuilder()
                .setId(id)
                .setFirstName(user
                        .getUserPersonalData()
                        .getFirstName())
                .setCreatedAt(user.getCreatedAt())
                .setModifiedAt(user.getModifiedAt())
                .createUserPersonalData();

        return new UserBuilder()
                .setId(id)
                .setEmail(createRandomEmail())
                .setGoogleId(user.getGoogleId())
                .setGoogleIdToken(user.getGoogleIdToken())
                .setCreatedAt(user.getCreatedAt())
                .setModifiedAt(user.getModifiedAt())
                .setUserPersonalData(userPersonalData)
                .createUser();
    }


    public String createRandomEmail() {
        Instant now = Instant.now();
        return now.toEpochMilli() + "@gmail.com";
    }


    public String createRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
