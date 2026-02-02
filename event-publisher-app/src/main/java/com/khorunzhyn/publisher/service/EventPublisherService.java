package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.EventMessageDto;
import com.khorunzhyn.publisher.enums.OutboxStatus;
import com.khorunzhyn.publisher.mapper.EventMapper;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.model.OutboxEvent;
import com.khorunzhyn.publisher.util.PublisherDataUtils;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final EventService eventService;
    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Scheduled(
            fixedDelayString = "${publisher.interval.ms:5000}",
            initialDelayString = "${publisher.initial-delay.ms:10000}"
    )
    @Transactional
    public void generateEvent() {
        try {
            // Generate event
            Event event = eventService.generateEvent();
            log.info("Generated event: {} (Type: {}) saved in publisher db",
                    event.getId(), event.getEventType());

            //save outbox event
            EventMessageDto eventMessageDto = EventMapper.totEventMessageDto(event);
            OutboxEvent outbox = buildOutboxEvent(eventMessageDto, event);
            OutboxEvent outboxEvent = outboxEventService.saveOutboxEvent(outbox);
            log.info("Outbox event {} saved for export", outboxEvent.getId());

            //metrics
            meterRegistry.counter("events.generated", "type", event.getEventType().name()).increment();

        } catch (Exception e) {
            log.error("Failed to generate or save event: {}", e.getMessage(), e);
        }
    }

    private OutboxEvent buildOutboxEvent(EventMessageDto eventMessageDto, Event event) {

        String payload = objectMapper.writeValueAsString(eventMessageDto);

        return OutboxEvent.builder()
                .aggregateType(PublisherDataUtils.OUTBOX_EVENT)
                .aggregateId(event.getId())
                .type(event.getEventType().name())
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .build();
    }
}
