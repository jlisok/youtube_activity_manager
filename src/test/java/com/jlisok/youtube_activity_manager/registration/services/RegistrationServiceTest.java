package com.jlisok.youtube_activity_manager.registration.services;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.RandomRegistrationDto;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;


    private String userEmail;
    private RegistrationRequestDto validUserRequest;

    @BeforeEach
    void createRandomUser() {
        userEmail = UserUtils.createRandomEmail();
        validUserRequest = RandomRegistrationDto.createValidRegistrationDto(userEmail);
    }


    @Test
    @Transactional
    void registerUser_whenRegistrationDtoIsValid() throws RegistrationException {
        //given //when
        registrationService.addUserToDatabase(validUserRequest);
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        //then
        assertTrue(optionalUser.isPresent());
    }


    @Test
    @Transactional
    void registerUser_whenUserAlreadyExistsInDatabase() throws RegistrationException {
        //given
        User user = UserUtils.createUserInDatabase(validUserRequest);
        assertTrue(userRepository
                .findByEmail(user.getEmail())
                .isPresent());

        // when //then
        assertThrows(BadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(validUserRequest));
    }

}