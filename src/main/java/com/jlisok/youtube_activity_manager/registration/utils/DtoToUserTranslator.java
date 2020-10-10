package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.models.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DtoToUserTranslator {

    private final PasswordEncoder passwordEncoder;
    private final DtoToUserPersonalDataTranslator dtoToUserPersonalDataTranslator;


    @Autowired
    public DtoToUserTranslator(PasswordEncoder passwordEncoder, DtoToUserPersonalDataTranslator dtoToUserPersonalDataTranslator) {
        this.passwordEncoder = passwordEncoder;
        this.dtoToUserPersonalDataTranslator = dtoToUserPersonalDataTranslator;
    }

    public User translate(RegistrationRequestDto registrationRequestDto, Instant now, UUID id) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        UserPersonalData userPersonalData = dtoToUserPersonalDataTranslator.translate(registrationRequestDto, now, id);

        String userPassword = passwordEncoder.encode(registrationRequestDto.getPassword());
        return new UserBuilder()
                .setId(userPersonalData.getId())
                .setPassword(userPassword)
                .setEmail(registrationRequestDto.getEmail())
                .setCreatedAt(now)
                .setModifiedAt(now)
                .setUserPersonalData(userPersonalData)
                .createUser();
    }

}
