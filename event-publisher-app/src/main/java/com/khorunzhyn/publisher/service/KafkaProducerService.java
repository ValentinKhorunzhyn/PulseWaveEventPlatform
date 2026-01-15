package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.EventMessageDto;
import com.khorunzhyn.publisher.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PublisherIdentityService identityService;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate, PublisherIdentityService identityService) {
        this.kafkaTemplate = kafkaTemplate;
        this.identityService = identityService;
    }

    private static final String EVENTS_TOPIC = "events.topic";

    public void sendEvent(Event event) {
        EventMessageDto message = new EventMessageDto(
                event.getId(),
                identityService.getPublisherId(),
                identityService.getMetadata(),
                event.getEventType(),
                event.getPayload(),
                event.getCreatedAt()
        );

        sendMessage(EVENTS_TOPIC, event.getId(), message);
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
                        topic, ex.getMessage());
            }
        });
    }

}
