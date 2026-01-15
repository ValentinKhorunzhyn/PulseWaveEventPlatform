package com.khorunzhyn.registar.service;

import com.khorunzhyn.registar.dto.ConfirmationMessageDto;
import com.khorunzhyn.registar.model.RegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CONFIRMATIONS_TOPIC = "confirmations.topic";

    public void sendConfirmation(RegisteredEvent event, String confirmationId) {
        ConfirmationMessageDto message = new ConfirmationMessageDto(
                event.getEventId(),
                event.getSourcePublisherId(),
                confirmationId,
                Instant.now()
        );

        sendMessage(CONFIRMATIONS_TOPIC, event.getEventId(), message);
    }

    private void sendMessage(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Successfully sent message to topic {}: {}",
                        topic, result.getRecordMetadata());
            } else {
                log.error("Failed to send message to topic {}: {}",
                        topic, ex.getMessage(), ex);
            }
        });
    }

}
