package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationDataProcessingException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestDtoToUserTranslatorTest {


    @Autowired
    private DtoToUserTranslator translator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUtils userUtils;


    private RegistrationRequestDto dto;
    private Instant now;
    private UUID id;


    @BeforeAll
    void createInitialVariables() {
        String userEmail = userUtils.createRandomEmail();
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
    void translate_whenDtoIsValid() throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException, RegistrationDataProcessingException {
        //given
        UserPersonalData userPersonalData = new UserPersonalData(
                id,
                dto.getGender(),
                dto.getBirthYear(),
                dto.getCountry(),
                dto.getPhonePrefix(),
                dto.getPhoneNumber(),
                dto.getFirstName(),
                now,
                now
        );

        User expected = new User(
                id,
                passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(),
                now,
                now,
                userPersonalData
        );

        //when
        User actual = translator.translate(dto, now, id);

        //then
        userUtils.assertUsersAreEqualWhenIgnoringPassword(expected, actual);
    }

    @Test
    void translate_whenDtoIsInValid() {
        //when
        RegistrationRequestDto registrationRequestDto = null;

        // then
        Assertions.assertThrows(RegistrationDataProcessingException.class, () -> translator.translate(registrationRequestDto, now, id));
    }


}