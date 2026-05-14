package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.enums.OutboxStatus;
import com.khorunzhyn.publisher.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxAckListener {

    private final OutboxEventRepository repository;

    @KafkaListener(topics = "events.topic",
            groupId = "publisher-ack-group",
            containerFactory = "ackContainerFactory")
    @Transactional
    public void onMessage(@Header(KafkaHeaders.RECEIVED_KEY) String aggregateId) {
        try {
            log.info("Marking event as sent: {}", aggregateId);
            if (aggregateId == null) {
                log.warn("Message has no eventId: {}", aggregateId);
                return;
            }

            repository.findByAggregateId(aggregateId).ifPresent(outbox -> {

                if (outbox.getStatus() == OutboxStatus.SENT) {
                    return;
                }

                outbox.setStatus(OutboxStatus.SENT);
                outbox.setProcessedAt(Instant.now());
                repository.save(outbox);

                log.debug("Outbox event {} marked as SENT", aggregateId);
            });

        } catch (Exception e) {
            log.error("Failed to process ack for message", e);
        }
    }

}
