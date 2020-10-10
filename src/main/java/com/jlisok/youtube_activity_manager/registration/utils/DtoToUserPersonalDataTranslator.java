package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalDataBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DtoToUserPersonalDataTranslator {


    public UserPersonalData translate(RegistrationRequestDto registrationRequestDto, Instant now, UUID id) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        checkIfWholePhoneNumberIsNullOrFilledIn(registrationRequestDto.getPhonePrefix(), registrationRequestDto.getPhoneNumber());
        return new UserPersonalDataBuilder()
                .setId(id)
                .setGender(registrationRequestDto.getGender())
                .setBirthYear(registrationRequestDto.getBirthYear())
                .setCountry(registrationRequestDto.getCountry())
                .setPhonePrefix(registrationRequestDto.getPhonePrefix())
                .setPhoneNumber(registrationRequestDto.getPhoneNumber())
                .setFirstName(registrationRequestDto.getFirstName())
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createUserPersonalData();
    }

    private void checkIfWholePhoneNumberIsNullOrFilledIn(String phonePrefix, String phoneNumber) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        if ((phoneNumber == null && phonePrefix != null) || (phoneNumber != null && phonePrefix == null)) {
            throw new PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException("User bad request - mobile prefix " + phonePrefix + " and number " + phoneNumber + " failed to pass requirements. Both numbers must be either null or filled in.");
        }
    }
}
