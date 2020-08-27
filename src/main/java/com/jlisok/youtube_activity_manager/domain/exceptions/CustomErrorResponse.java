package com.jlisok.youtube_activity_manager.domain.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;


public class CustomErrorResponse {

    private final UUID id;
    private final ResponseCode responseCode;
    private final String detailedMessage;
    @JsonIgnore
    private final HttpStatus httpStatus;


    public CustomErrorResponse(ResponseCode responseCode, HttpStatus httpStatus, String detailedMessage) {
        this.id = UUID.randomUUID();
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
        this.detailedMessage = detailedMessage;
    }


    public CustomErrorResponse(ResponseCode responseCode, HttpStatus httpStatus) {
        this.id = UUID.randomUUID();
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
        this.detailedMessage = "";
    }

    public CustomErrorResponse(ResponseCode responseCode) {
        this.id = UUID.randomUUID();
        this.responseCode = responseCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.detailedMessage = "";
    }

    @JsonCreator
    public CustomErrorResponse(@JsonProperty("ResponseCode") ResponseCode responseCode,
                               @JsonProperty("detailedMessage") String detailedMessage,
                               @JsonProperty("id") UUID id,
                               HttpStatus httpStatus) {

        this.id = id;
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
        this.detailedMessage = detailedMessage;
    }

    public UUID getId() {
        return id;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomErrorResponse that = (CustomErrorResponse) o;
        return Objects.equals(responseCode, that.responseCode) &&
                detailedMessage.equals(that.detailedMessage) &&
                httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseCode, detailedMessage, httpStatus);
    }
}
