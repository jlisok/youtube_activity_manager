package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;

public class RandomRegistrationDto {

    public static RegistrationRequestDto createValidRegistrationDto(String email) {
        return new RegistrationRequestDto(
                "1111111111111111111111",
                email,
                Sex.FEMALE,
                2000,
                "Poland",
                "0045",
                "600600600",
                "John");
    }


    public static RegistrationRequestDto createValidRegistrationDto(String email, String password) {
        return new RegistrationRequestDto(
                password,
                email,
                Sex.FEMALE,
                2000,
                "Poland",
                "0045",
                "600600600",
                "John");
    }


}

