package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootTest
class RequestDtoToUserPersonalDataTranslatorTest {

    @Autowired
    private UserUtils userUtils;

    private static String userEmail;
    private RegistrationRequestDto dto;
    private Instant now;
    private UUID id;
    private DtoToUserPersonalDataTranslator translator;


    @BeforeEach
    void createRandomUser() {
        userEmail = userUtils.createRandomEmail();
        dto = new RegistrationRequestDto(
                "1111",
                userEmail,
                Sex.FEMALE,
                2000,
                "DD",
                null,
                null,
                null);
        now = Instant.now();
        id = UUID.randomUUID();
        translator = new DtoToUserPersonalDataTranslator();
    }

    @Test
    void translate_whenDtoIsValid() throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        //given
        UserPersonalData expected = new UserPersonalData(
                id,
                dto.getGender(),
                dto.getBirthYear(),
                dto.getCountry(),
                dto.getPhonePrefix(),
                dto.getPhoneNumber(),
                dto.getFirstName(),
                now,
                now);


        //when
        UserPersonalData actual = translator.translate(dto, now, id);

        //then
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("addUser_whenUserFailsToFillInPhoneNumberOrPhonePrefixTestData")
    void addUser_whenUserFailsToFillInPhoneNumberOrPhonePrefix(RegistrationRequestDto registrationRequestDto) {
        //given // when // then
        Assertions.assertThrows(PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException.class, () -> translator.translate(registrationRequestDto, now, id));
    }


    static Stream<Arguments> addUser_whenUserFailsToFillInPhoneNumberOrPhonePrefixTestData() {

        RegistrationRequestDto validUserRequest = new RegistrationRequestDto(
                "1111111111111111111111",
                userEmail,
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
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                null,
                                validUserRequest.getPhoneNumber(),
                                validUserRequest.getFirstName()
                        )),
                //2
                Arguments.arguments(
                        new RegistrationRequestDto(
                                validUserRequest.getPassword(),
                                validUserRequest.getEmail(),
                                validUserRequest.getGender(),
                                validUserRequest.getBirthYear(),
                                validUserRequest.getCountry(),
                                validUserRequest.getPhonePrefix(),
                                null,
                                validUserRequest.getFirstName()
                        ))
        );
    }

}


