package com.jlisok.youtube_activity_manager.registration.dto;

import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class RegistrationRequestDto {

    @NotBlank
    private final String password;

    @NotBlank
    @Email
    private final String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private final Sex gender;

    @NotNull
    private final Integer birthYear;

    @NotBlank
    private final String country;

    @Pattern(regexp = "(\\+|00)[0-9]{1,3}")
    private final String phonePrefix;

    @Pattern(regexp = "[0-9]{9}")
    private final String phoneNumber;

    private final String firstName;

    public RegistrationRequestDto(String password, String email, Sex gender, Integer birthYear, String country, String phonePrefix, String phoneNumber, String firstName) {
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.birthYear = birthYear;
        this.country = country;
        this.phonePrefix = phonePrefix;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;

    }


    public String getPhonePrefix() {
        return phonePrefix;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Sex getGender() {
        return gender;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

}
