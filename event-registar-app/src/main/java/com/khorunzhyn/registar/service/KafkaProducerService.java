package com.khorunzhyn.registar.service;

import com.khorunzhyn.registar.dto.ConfirmationMessageDto;
import com.khorunzhyn.registar.model.RegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String CONFIRMATIONS_TOPIC = "confirmations.topic";

    // Blocking call -> wait kafka response
    public void sendConfirmationSync(RegisteredEvent event) throws ExecutionException, InterruptedException {
        ConfirmationMessageDto message = new ConfirmationMessageDto(
                event.getEventId(),
                event.getSourcePublisherId(),
                event.getConfirmationId(),
                Instant.now()
        );

        String confirmationJson = objectMapper.writeValueAsString(message);

        // .get() make a sync call. If kafka is unavailable than throw exception
        kafkaTemplate.send(CONFIRMATIONS_TOPIC, event.getSourcePublisherId(), confirmationJson).get();

        log.debug("Confirmation sent for event {}", event.getEventId());
    }
}
