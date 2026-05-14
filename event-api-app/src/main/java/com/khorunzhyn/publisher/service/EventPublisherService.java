package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.EventDto;
import com.khorunzhyn.publisher.dto.EventMessageDto;
import com.khorunzhyn.publisher.enums.OutboxStatus;
import com.khorunzhyn.publisher.mapper.EventMapper;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.model.OutboxEvent;
import com.khorunzhyn.publisher.repository.EventRepository;
import com.khorunzhyn.publisher.util.PublisherDataUtils;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final OutboxEventService outboxEventService;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void handleEvent(EventDto eventDto) {
        try {
            log.info("Received event: {} (Type: {})", eventDto.getId(), eventDto.getEventType());
            //map event
            Event event = EventMapper.toEvent(eventDto);
            //save event
            Event savedEvent = eventRepository.save(event);
            log.debug("Event saved with id: {}", savedEvent.getId());

            //save outbox event
            EventMessageDto eventMessageDto = EventMapper.toEventMessageDto(event);
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
