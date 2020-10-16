package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalDataBuilder;
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
class RequestDtoToUserPersonalDataTranslatorTest implements TestProfile {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DtoToUserPersonalDataTranslator translator;

    private static String userEmail;
    private RegistrationRequestDto dto;
    private Instant now;
    private UUID id;


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
    }

    @Test
    void translate_whenDtoIsValid() throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        //given
        UserPersonalData expected = new UserPersonalDataBuilder()
                .setId(id)
                .setGender(dto.getGender())
                .setBirthYear(dto.getBirthYear())
                .setCountry(dto.getCountry())
                .setPhonePrefix(dto.getPhonePrefix())
                .setPhoneNumber(dto.getPhoneNumber())
                .setFirstName(dto.getFirstName())
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createUserPersonalData();


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


