package com.jlisok.youtube_activity_manager.userPersonalData.models;

import com.jlisok.youtube_activity_manager.database.SexEnumTypePostgreSql;
import com.jlisok.youtube_activity_manager.userPersonalData.enums.Sex;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_personal_data")
@TypeDef(name = "sex", typeClass = SexEnumTypePostgreSql.class)
public class UserPersonalData {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    @Type(type = "sex")
    private Sex gender;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "country")
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public UserPersonalData() {
    }

    public UserPersonalData(UUID id, Sex gender, Integer birthYear, String country, String phonePrefix, String phoneNumber, String firstName, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.gender = gender;
        this.birthYear = birthYear;
        this.country = country;
        this.phoneNumber = phonePrefix + phoneNumber;
        this.firstName = firstName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Enum<Sex> getGender() {
        return gender;
    }

    public void setGender(Sex gender) {
        this.gender = gender;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPersonalData that = (UserPersonalData) o;
        return id.equals(that.id) &&
                gender == that.gender &&
                birthYear.equals(that.birthYear) &&
                country.equals(that.country) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(firstName, that.firstName) &&
                createdAt.equals(that.createdAt) &&
                modifiedAt.equals(that.modifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gender, birthYear, country, phoneNumber, firstName, createdAt, modifiedAt);
    }
}
