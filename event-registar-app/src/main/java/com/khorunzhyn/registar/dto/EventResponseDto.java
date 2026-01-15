package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Registered event response")
public class EventResponseDto {

    @Schema(description = "Registration service event ID")
    UUID id;

    @Schema(description = "Original event ID from publisher")
    String eventId;

    @Schema(description = "Source publisher ID")
    String sourcePublisherId;

    @Schema(description = "Event type")
    EventType eventType;

    @Schema(description = "Event payload (JSON)")
    Map<String, Object> payload;

    @Schema(description = "Publisher metadata")
    Map<String, String> publisherMetadata;

    @Schema(description = "Processing status")
    EventStatus eventStatus;

    @Schema(description = "When event was created by publisher")
    Instant originalCreatedAt;

    @Schema(description = "When event was received")
    Instant receivedAt;

    @Schema(description = "When event was processed")
    Instant processedAt;

    @Schema(description = "Error message if processing failed")
    String errorMessage;

    @Schema(description = "Confirmation ID sent back to publisher")
    String confirmationId;
}
