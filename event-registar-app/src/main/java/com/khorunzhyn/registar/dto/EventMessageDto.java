package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.khorunzhyn.registar.enums.EventType;
import com.khorunzhyn.registar.model.PublisherMetadata;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventMessageDto(
        @NotBlank
        @JsonAlias("id")
        @JsonProperty("eventId")
        String eventId,

        @NotBlank
        @JsonAlias("publisher_id")
        @JsonProperty("publisherId")
        String publisherId,

        @JsonAlias("publisher_metadata")
        @JsonProperty("publisherMetadata")
        PublisherMetadata publisherMetadata,

        @NotNull
        @JsonAlias("event_type")
        @JsonProperty("eventType")
        EventType eventType,

        @NotBlank
        @JsonAlias("payload")
        @JsonProperty("payload")
        String payload,

        @NotNull
        @JsonAlias("created_at")
        @JsonProperty("createdAt")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant createdAt
) {
}
