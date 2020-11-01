package com.jlisok.youtube_activity_manager.registration.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.FieldViolationBadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.exceptions.UnexpectedErrorBadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.testutils.RandomRegistrationDto;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RegistrationServiceTest implements TestProfile {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JWTVerifier jwtVerifier;

    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private RegistrationRequestDto dto;

    @BeforeEach
    void createRandomUser() {
        String userEmail = userUtils.createRandomEmail();
        dto = RandomRegistrationDto.createValidRegistrationDto(userEmail);
    }


    @Test
    void registerUser_whenRegistrationDtoIsValid() throws RegistrationException {
        //given //when
        String jwToken = registrationService.addUserToDatabase(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(jwToken);

        //then
        verify(userRepository).saveAndFlush(userCaptor.capture());
        String expectedUserId = userCaptor.getValue().getId().toString();
        Assertions.assertEquals(expectedUserId, decodedJWT.getSubject());
    }


    @Test
    void registerUser_whenUserEmailAlreadyExistsInDatabase() {
        //given
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database", new PSQLException("ERROR: duplicate key value violates unique constraint \"users_email_key\"", PSQLState.UNIQUE_VIOLATION)));

        // when //then
        assertThrows(FieldViolationBadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
    }


    @Test
    void registerUser_whenUserViolatedFieldInDatabaseOtherThanEmail() {
        //given
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database", new PSQLException("some other field violated", PSQLState.UNIQUE_VIOLATION)));

        // when //then
        assertThrows(UnexpectedErrorBadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
    }


    @Test
    void registerUser_whenSomeOtherUnexpectedException() {
        //given
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database"));

        // when //then
        assertThrows(UnexpectedErrorBadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
    }

}