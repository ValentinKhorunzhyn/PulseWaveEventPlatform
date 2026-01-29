package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.ConfirmationMessageDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EventService eventService;

    @KafkaListener(
            topics = "${kafka.topics.confirmations:confirmations.topic}",
            groupId = "${spring.kafka.consumer.group-id:publisher-group}",
            containerFactory = "confirmationContainerFactory"
    )
    @RetryableTopic(
            attempts = "5",
            backOff = @BackOff(delay = 1000, multiplier = 2.0)
    )
    public void consumeConfirmation(
            @Payload ConfirmationMessageDto confirmation,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment ack) {

        log.info("Received confirmation for event {} from Kafka - Key: {}, Partition: {}, Timestamp: {}",
                confirmation.eventId(), key, partition, timestamp);

        eventService.confirmEvent(confirmation);
        ack.acknowledge();

        log.debug("Successfully processed confirmation for event {}",
                confirmation.eventId());
    }

    @DltHandler
    public void handleDlt(ConfirmationMessageDto message,
                          @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        log.error("Failed to process confirmation for event {}: {}",
                message, errorMessage);
        eventService.markEventAsFailed(message.eventId(), errorMessage);
    }
}

