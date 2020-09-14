package com.jlisok.youtube_activity_manager.userPersonalData.models;

import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;

import java.time.Instant;
import java.util.UUID;

public class UserPersonalDataBuilder {

    private UUID id;
    private Sex gender = Sex.NOT_APPLICABLE;
    private Integer birthYear;
    private String country;
    private String phonePrefix;
    private String phoneNumber;
    private String firstName;
    private Instant createdAt;
    private Instant modifiedAt;

    public UserPersonalDataBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public UserPersonalDataBuilder setGender(Sex gender) {
        this.gender = gender;
        return this;
    }

    public UserPersonalDataBuilder setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
        return this;
    }

    public UserPersonalDataBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public UserPersonalDataBuilder setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
        return this;
    }

    public UserPersonalDataBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserPersonalDataBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserPersonalDataBuilder setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserPersonalDataBuilder setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public UserPersonalData createUserPersonalData() {
        return new UserPersonalData(id, gender, birthYear, country, phonePrefix, phoneNumber, firstName, createdAt, modifiedAt);
    }
}