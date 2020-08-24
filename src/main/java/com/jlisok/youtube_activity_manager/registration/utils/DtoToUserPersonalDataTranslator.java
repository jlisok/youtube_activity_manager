package com.jlisok.youtube_activity_manager.registration.utils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DtoToUserPersonalDataTranslator {


    public UserPersonalData translate(RegistrationRequestDto registrationRequestDto, Instant now, UUID id) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        checkIfWholePhoneNumberIsNullOrFilledIn(registrationRequestDto.getPhonePrefix(), registrationRequestDto.getPhoneNumber());
        return new UserPersonalData(id,
                registrationRequestDto.getGender(),
                registrationRequestDto.getBirthYear(),
                registrationRequestDto.getCountry(),
                registrationRequestDto.getPhonePrefix(),
                registrationRequestDto.getPhoneNumber(),
                registrationRequestDto.getFirstName(),
                now,
                now);
    }

    private void checkIfWholePhoneNumberIsNullOrFilledIn(String phonePrefix, String phoneNumber) throws PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException {
        if ((phoneNumber == null && phonePrefix != null) || (phoneNumber != null && phonePrefix == null)) {
            throw new PrefixAndPhoneNumberMustBeBothEitherNullOrFilledException("User bad request - mobile prefix " + phonePrefix + " and number " + phoneNumber + " failed to pass requirements. Both numbers must be either null or filled in.");
        }
    }
}
