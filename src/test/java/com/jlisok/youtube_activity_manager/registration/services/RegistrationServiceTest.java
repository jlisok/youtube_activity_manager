package com.jlisok.youtube_activity_manager.registration.services;

import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.registration.utils.DtoToUserTranslator;
import com.jlisok.youtube_activity_manager.testutils.RandomRegistrationDto;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DtoToUserTranslator userTranslator;

    @MockBean
    private UserRepository userRepository;

    private RegistrationRequestDto dto;
    private RegistrationService registrationService;

    @BeforeEach
    void createRandomUser() {
        String userEmail = userUtils.createRandomEmail();
        dto = RandomRegistrationDto.createValidRegistrationDto(userEmail);
        registrationService = new RegistrationService(userRepository, userTranslator);
    }


    @Test
    void registerUser_whenRegistrationDtoIsValid() throws RegistrationException {
        //given //when
        registrationService.addUserToDatabase(dto);

        //then
        verify(userRepository).saveAndFlush(any(User.class));
    }


    @Test
    void registerUser_whenUserEmailAlreadyExistsInDatabase() {
        //given
        CustomErrorResponse expectedCustomErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_VIOLATED_FIELD_EMAIL, HttpStatus.BAD_REQUEST, "PSQLState: 23505");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database", new PSQLException("email", PSQLState.UNIQUE_VIOLATION)));

        // when //then
        BadRegistrationRequestException exception = assertThrows(BadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
        assertEquals(expectedCustomErrorResponse, exception.getCustomErrorResponse());
    }




    @Test
    void registerUser_whenUserViolatedFieldInDatabaseOtherThanEmail() {
        //given
        CustomErrorResponse expectedCustomErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "PSQLState: 23505");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database", new PSQLException("some other field violated", PSQLState.UNIQUE_VIOLATION)));

        // when //then
        BadRegistrationRequestException exception = assertThrows(BadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
        assertEquals(expectedCustomErrorResponse, exception.getCustomErrorResponse());
    }



    @Test
    void registerUser_whenSomeOtherUnexpectedException() {
        //given
        CustomErrorResponse expectedCustomErrorResponse = new CustomErrorResponse(ResponseCode.REGISTRATION_FAILED_UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database"));

        // when //then
        BadRegistrationRequestException exception = assertThrows(BadRegistrationRequestException.class, () -> registrationService.addUserToDatabase(dto));
        assertEquals(expectedCustomErrorResponse, exception.getCustomErrorResponse());
    }

}