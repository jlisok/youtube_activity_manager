package com.jlisok.youtube_activity_manager.registration.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.BadRegistrationRequestException;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerTest implements TestProfile {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvcBasicRequestBuilder mvcBasicRequestBuilder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private JwtTokenVerifier tokenVerifier;

    @Autowired
    private MvcResponseVerifier mvcResponseVerifier;

    private final String endPointUrl = "/api/v1/registration";

    private RegistrationRequestDto dto;

    @BeforeEach
    void createRandomUser() {
        String userEmail = userUtils.createRandomEmail();
        dto = RandomRegistrationDto.createValidRegistrationDto(userEmail);
    }

    @Test
    @Transactional
    void addUser_whenUserIsValid() throws Exception {
        //given

        //when //then
        mockMvc.perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dto))
               .andExpect(status().isOk())
               .andExpect(result -> tokenVerifier.assertTokenNotNull(result));

        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        assertTrue(user.isPresent());
    }


    @Test
    @Transactional
    void addUser_whenUserExistsInDatabase() throws Exception {
        //given
        userUtils.insertUserInDatabase(dto);
        ResponseCode expected = ResponseCode.REGISTRATION_FAILED_VIOLATED_FIELD_EMAIL;

        //when //then
        mockMvc.perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dto))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRegistrationRequestException))
               .andExpect(result -> mvcResponseVerifier.assertEqualsResponseCode(expected, result));
    }


    @ParameterizedTest
    @MethodSource("addUser_whenUserFailsValidationTestData")
    @Transactional
    void addUser_whenUserFailsValidation(RegistrationRequestDto registrationRequestDto) throws Exception {
        //given //when //then
        mockMvc.perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, registrationRequestDto))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    Stream<Arguments> addUser_whenUserFailsValidationTestData() {

        RegistrationRequestDto validUserRequest = new RegistrationRequestDto(
                "1111111111111111111111",
                userUtils.createRandomEmail(),
                Sex.FEMALE,
                2000,
                "Poland",
                "0045",
                "600600600",
                "John");

        return Stream.of(
                //1
                Arguments.arguments(
                        new RegistrationRequestDto(
                                null,
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )
                ),
                //2
                Arguments.arguments(
                        new RegistrationRequestDto(
                                "",
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //3
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                null,
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //4
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                "",
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //5
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                "mmzz",
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //6
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                null,
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //7
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                null,
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //8
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                null,
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //9
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                "",
                                validUserRequest.getPhonePrefix(),
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //10
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                "0067832",
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //11
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                "++876",
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //12
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                "600600",
                                validUserRequest.getFirstName()
                        )),
                //13
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                "9oiggj",
                                validUserRequest.getFirstName()
                        ))
        );
    }


}