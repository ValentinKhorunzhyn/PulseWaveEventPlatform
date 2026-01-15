package com.khorunzhyn.registar.mapper;

import com.khorunzhyn.registar.dto.EventMessageDto;
import com.khorunzhyn.registar.dto.EventResponseDto;
import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.model.RegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventMapper {

    private final ObjectMapper objectMapper;

    public RegisteredEvent toEntity(EventMessageDto message) {
        try {
            JsonNode payloadNode = objectMapper.readTree(message.payload());
            Map<String, Object> publisherMetadata = Map.of();
            if (message.publisherMetadata() != null) {
                publisherMetadata = objectMapper.convertValue(
                        message.publisherMetadata(),
                        new TypeReference<Map<String, Object>>() {
                        }
                );
            }

            return RegisteredEvent.builder()
                    .eventId(message.eventId())
                    .sourcePublisherId(message.publisherId())
                    .eventType(message.eventType())
                    .payload(payloadNode.toString())
                    .publisherMetadata(publisherMetadata)
                    .originalCreatedAt(message.createdAt())
                    .receivedAt(Instant.now())
                    .processingStatus(EventStatus.RECEIVED)
                    .build();

        } catch (JacksonException ex) {
            log.error("Failed to parse payload for event {}", message.eventId(), ex);
            throw new IllegalArgumentException("Invalid payload format", ex);
        }
    }

    public EventResponseDto toResponse(RegisteredEvent event) {
        try {
            Map<String, Object> payloadMap = objectMapper.readValue(
                    event.getPayload(),
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, String> metadataMap = event.getPublisherMetadata() != null ?
                    objectMapper.convertValue(event.getPublisherMetadata(),
                            new TypeReference<Map<String, String>>() {
                            }) :
                    Map.of();

            return EventResponseDto.builder()
                    .id(event.getId())
                    .eventId(event.getEventId())
                    .sourcePublisherId(event.getSourcePublisherId())
                    .eventType(event.getEventType())
                    .payload(payloadMap)
                    .publisherMetadata(metadataMap)
                    .eventStatus(event.getProcessingStatus())
                    .originalCreatedAt(event.getOriginalCreatedAt())
                    .receivedAt(event.getReceivedAt())
                    .processedAt(event.getProcessedAt())
                    .errorMessage(event.getErrorMessage())
                    .confirmationId(event.getConfirmationId())
                    .build();

        } catch (Exception ex) {
            log.error("Failed to convert event {} to response", event.getId(), ex);
            throw new IllegalArgumentException("Exception in building response", ex);
        }
    }

}
