package com.khorunzhyn.registar.service;

import com.khorunzhyn.registar.dto.EventMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EventRegistrationService eventRegistrationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.events:events.topic}",
            groupId = "${spring.kafka.consumer.group-id:registration-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2.0)
    )
    public void consumeEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment ack) {

        log.info("Received event from Kafka - Key: {}, Partition: {}, Timestamp: {}",
                key, partition, timestamp);
        EventMessageDto messageDto = parseMessage(message);
        eventRegistrationService.registerEvent(messageDto);

        ack.acknowledge();
        log.info("Successfully processed event: {}", messageDto.eventId());
    }

    @DltHandler
    public void handleDlt(String message,
                          @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        log.error("Event failed after all retries. Moving to DLT. Message: {}, Error: {}", message, errorMessage);
        EventMessageDto messageDto = parseMessage(message);
        saveFailedEvent(messageDto, new RuntimeException(errorMessage));
    }

    private EventMessageDto parseMessage(String message) {
        JsonNode node = objectMapper.readTree(message);
        if (node.isString()) {
            node = objectMapper.readTree(node.asString());
        }
        return objectMapper.treeToValue(node, EventMessageDto.class);
    }

    private void saveFailedEvent(EventMessageDto message, Exception exception) {
        try {

            log.warn("Saving failed event details to database: {}", message.eventId());
            //todo business logic for saving failed events
        } catch (Exception e) {
            log.error("Failed to save failed event {} to database: {}",
                    message.eventId(), e.getMessage(), e);
        }
    }

}
