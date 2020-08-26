package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.utils.DtoToUserTranslator;
import com.jlisok.youtube_activity_manager.users.models.User;
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


    public void createUserInDatabase(RegistrationRequestDto dto) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = dtoToUserTranslator.translate(dto, now, id);
        repository.save(user);

        assertTrue(repository
                .findByEmail(user.getEmail())
                .isPresent());

    }


    public User createUserInDatabase(String userEmail, String userPassword) throws RegistrationException {
        User user = createUser(userEmail, userPassword);
        repository.save(user);

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


    public String createRandomEmail() {
        Instant now = Instant.now();
        return now.toEpochMilli() + "@gmail.com";
    }


    public String createRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(20);
    }


}
