package com.jlisok.youtube_activity_manager.domain.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;


public class CustomErrorResponse {

    private final UUID id;
    private final ResponseCode responseCode;


    public CustomErrorResponse(ResponseCode responseCode) {
        this.id = UUID.randomUUID();
        this.responseCode = responseCode;
    }


    @JsonCreator
    public CustomErrorResponse(@JsonProperty("ResponseCode") ResponseCode responseCode,
                               @JsonProperty("id") UUID id) {
        this.id = id;
        this.responseCode = responseCode;
    }

    public UUID getId() {
        return id;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomErrorResponse that = (CustomErrorResponse) o;
        return Objects.equals(responseCode, that.responseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseCode);
    }
}
