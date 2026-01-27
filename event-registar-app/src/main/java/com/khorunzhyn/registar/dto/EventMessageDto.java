package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.khorunzhyn.registar.enums.EventType;
import com.khorunzhyn.registar.model.PublisherMetadata;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventMessageDto(
        @NotBlank
        @JsonProperty("eventId")
        String eventId,

        @NotBlank
        @JsonProperty("publisherId")
        String publisherId,

        @JsonProperty("publisherMetadata")
        PublisherMetadata publisherMetadata,

        @NotNull
        @JsonProperty("eventType")
        EventType eventType,

        @NotBlank
        @JsonProperty("payload")
        String payload,

        @NotNull
        @JsonProperty("createdAt")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant createdAt
) {
}
