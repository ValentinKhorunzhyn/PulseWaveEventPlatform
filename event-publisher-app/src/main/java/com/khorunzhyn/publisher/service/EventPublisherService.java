package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final EventService eventService;
    private final KafkaProducerService kafkaProducerService;

    private final AtomicLong eventsGenerated = new AtomicLong(0);
    private final AtomicLong eventsSent = new AtomicLong(0);

    @Scheduled(
            fixedDelayString = "${publisher.interval.ms:5000}",
            initialDelayString = "${publisher.initial-delay.ms:10000}"
    )
    public void generateAndSendEvent() {
        try {
            // Generate event
            Event event = eventService.generateEvent();
            eventsGenerated.incrementAndGet();
            // send to Kafka
            kafkaProducerService.sendEvent(event);
            eventsSent.incrementAndGet();

            log.info("Generated and sent event: {} (Type: {})",
                    event.getId(), event.getEventType());

        } catch (Exception e) {
            log.error("Failed to generate or send event: {}", e.getMessage(), e);
        }
    }

}
