package com.khorunzhyn.publisher.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ConfirmationMessageDto(
        @NotBlank
        @JsonProperty("eventId")
        String eventId,

        @NotBlank
        @JsonProperty("generatorId")
        String generatorId,

        @NotBlank
        @JsonProperty("registrationId")
        String registrationId,

        @NotNull
        @JsonProperty("confirmedAt")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant confirmedAt
) {
}

