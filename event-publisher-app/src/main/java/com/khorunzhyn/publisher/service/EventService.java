package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.ConfirmationMessageDto;
import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.enums.EventType;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final EventPayloadFactory payloadFactory;
    private final PublisherIdentityService identityService;

    public EventService(EventRepository eventRepository, EventPayloadFactory payloadFactory, PublisherIdentityService identityService) {
        this.eventRepository = eventRepository;
        this.payloadFactory = payloadFactory;
        this.identityService = identityService;
    }

    @Transactional
    public Event generateEvent() {
        EventType eventType = EventType.getRandomEventType();
        String payload = payloadFactory.createPayload(eventType);

        Event event = Event.builder()
                .eventType(eventType)
                .payload(payload)
                .status(EventStatus.CREATED)
                .publisherId(identityService.getPublisherId())
                .publisherMetadata(identityService.getMetadataForEvent())
                .build();

        Event savedEvent = eventRepository.save(event);
        log.debug("Generated event: {} of type {}", savedEvent.getId(), eventType);

        return savedEvent;
    }

    public Optional<Event> findEvent(String eventId) {
        return eventRepository.findById(eventId);
    }

    @Transactional
    public void confirmEvent(ConfirmationMessageDto confirmation) {
        String eventId = confirmation.eventId();
        eventRepository.findById(eventId)
                .ifPresentOrElse(
                        event -> {
                            event.setStatus(EventStatus.CONFIRMED);
                            event.setConfirmedAt(confirmation.confirmedAt());
                            eventRepository.save(event);
                            log.info("Event {} confirmed by registration service", eventId);
                        },
                        () -> log.warn("Event {} not found for confirmation", eventId)
                );
    }

    public void markEventAsFailed(String eventId, String reason) {
        eventRepository.findById(eventId)
                .ifPresent(event -> {
                    event.setStatus(EventStatus.FAILED);
                    eventRepository.save(event);
                    log.warn("Event {} marked as failed: {}", eventId, reason);
                });
    }

    public List<Event> getPendingEvents(int limit) {
        return eventRepository.findByStatusAndCreatedAtBefore(
                        EventStatus.CREATED,
                        Instant.now().minusSeconds(60))
                .stream()
                .limit(limit)
                .toList();
    }

    public List<Event> getRecentEvents(int limit) {
        return eventRepository.findAll()
                .stream()
                .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
}
