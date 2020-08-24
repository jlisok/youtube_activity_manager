package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.utils.DtoToUserTranslator;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.Objects;

import java.util.UUID;

@Component
public class UserUtils {

    private static DtoToUserTranslator dtoToUserTranslator;
    private static UserRepository repository;

    @Autowired
    public UserUtils(DtoToUserTranslator dtoToUserTranslator, UserRepository userRepository) {
        this.dtoToUserTranslator = dtoToUserTranslator;
        this.repository = userRepository;
    }


    public static void assertUsersAreEqualWhenIgnoringPassword(User expected, User actual) {
        expected.setPassword("");
        actual.setPassword("");
        Assertions.assertEquals(expected, actual);
    }


    public static void assertUsersAreEqualWhenIgnoringPasswordAndAllowingNulls(User expected, User actual) {
        expected.setPassword("");
        actual.setPassword("");
        boolean isEqual = checkIfExpectedUserEqualsActualUserWhenBothIncludeNullFields(expected, actual);
        Assertions.assertTrue(isEqual);
    }


    private static boolean checkIfExpectedUserEqualsActualUserWhenBothIncludeNullFields(User expected, User actual) {
        if (expected == actual) return true;
        if (expected == null) return false;
        return Objects.equals(expected.getId(), actual.getId()) &&
                Objects.equals(expected.getPassword(), actual.getPassword()) &&
                Objects.equals(expected.getEmail(), actual.getEmail()) &&
                Objects.equals(expected.getCreatedAt(), actual.getCreatedAt()) &&
                Objects.equals(expected.getModifiedAt(), actual.getModifiedAt()) &&
                Objects.equals(expected.getUserPersonalData(), actual.getUserPersonalData());
    }


    public static User createUserInDatabase(RegistrationRequestDto dto) throws RegistrationException {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        User user = dtoToUserTranslator.translate(dto, now, id);
        repository.save(user);
        return user;
    }


    public static String createRandomEmail() {
        Instant now = Instant.now();
        return now.toEpochMilli() + "@gmail.com";
    }

    public static String createRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(20);
    }


}
