package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.client.EventApiClient;
import com.khorunzhyn.publisher.model.Event;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final EventService eventService;
    private final MeterRegistry meterRegistry;
    private final EventApiClient eventApiClient;

    @Scheduled(
            fixedDelayString = "${publisher.interval.ms:5000}",
            initialDelayString = "${publisher.initial-delay.ms:10000}"
    )
    public void generateAndSendEvent() {
        try {
            // Generate event
            Event event = eventService.generateEvent();
            log.info("Generated event: {}", event.getEventType());

            //metrics
            meterRegistry.counter("events.generated", "type", event.getEventType().name()).increment();

            eventApiClient.sendEvent(event.toDto());
            log.info("Successfully sent event");
        } catch (Exception e) {
            log.error("Failed to generate or send event: {}", e.getMessage(), e);
        }
    }
}
