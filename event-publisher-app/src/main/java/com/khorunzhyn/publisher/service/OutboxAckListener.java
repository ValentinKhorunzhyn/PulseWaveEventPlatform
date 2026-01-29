package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.enums.OutboxStatus;
import com.khorunzhyn.publisher.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxAckListener {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "events.topic",
            groupId = "publisher-ack-group",
            containerFactory = "stringContainerFactory")
    @Transactional
    public void onMessage(String message) {
        try {

            JsonNode node = objectMapper.readTree(message);

            if (node.isString()) {
                node = objectMapper.readTree(node.asString());
            }

            JsonNode eventIdNode = node.get("eventId");

            if (eventIdNode == null) {
                log.warn("Message has no eventId: {}", message);
                return;
            }

            String eventId = eventIdNode.asString();

            repository.findByAggregateId(eventId).ifPresent(outbox -> {

                if (outbox.getStatus() == OutboxStatus.SENT) {
                    return;
                }

                outbox.setStatus(OutboxStatus.SENT);
                outbox.setProcessedAt(Instant.now());
                repository.save(outbox);

                log.debug("Outbox event {} marked as SENT", eventId);
            });

        } catch (Exception e) {
            log.error("Failed to process ack for message", e);
        }
    }

}
