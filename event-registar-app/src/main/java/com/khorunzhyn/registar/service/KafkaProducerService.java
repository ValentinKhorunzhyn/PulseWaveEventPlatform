package com.khorunzhyn.registar.service;

import com.khorunzhyn.registar.avro.ConfirmationEventAvro;
import com.khorunzhyn.registar.model.RegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CONFIRMATIONS_TOPIC = "confirmations.topic";

    // Blocking call -> wait kafka response
    public void sendConfirmationSync(RegisteredEvent event) throws ExecutionException, InterruptedException {

        // 1. Map Entity into Avro
        ConfirmationEventAvro avroMessage = ConfirmationEventAvro.newBuilder()
                .setEventId(event.getEventId())
                .setPublisherId(event.getSourcePublisherId())
                .setRegistrationId(event.getConfirmationId())
                .setConfirmedAt(Instant.now())
                .build();

        // 2. Send to kafka (Key = eventId, Value = Avro)
        kafkaTemplate.send(CONFIRMATIONS_TOPIC, avroMessage.getEventId(), avroMessage).get();

        log.info("Successfully sent Avro confirmation for event: {}", event.getEventId());
    }
}
