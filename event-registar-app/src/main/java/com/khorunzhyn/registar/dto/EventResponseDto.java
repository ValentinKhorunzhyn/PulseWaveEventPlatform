package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import com.khorunzhyn.registar.model.PublisherMetadata;
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
    private UUID id;

    @Schema(description = "Original event ID from publisher")
    private String eventId;

    @Schema(description = "Source publisher ID")
    private String sourcePublisherId;

    @Schema(description = "Event type")
    private EventType eventType;

    @Schema(description = "Event payload (JSON)")
    private Map<String, Object> payload;

    @Schema(description = "Publisher metadata")
    private PublisherMetadata publisherMetadata;

    @Schema(description = "Processing status")
    private EventStatus eventStatus;

    @Schema(description = "When event was created by publisher")
    private Instant originalCreatedAt;

    @Schema(description = "When event was received")
    private Instant receivedAt;

    @Schema(description = "When event was processed")
    private Instant processedAt;

    @Schema(description = "Error message if processing failed")
    private String errorMessage;

    @Schema(description = "Confirmation ID sent back to publisher")
    private String confirmationId;
}
