package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.repository.EventRepository;
import com.khorunzhyn.common.avro.ConfirmationEventAvro;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Optional<Event> findEvent(String eventId) {
        return eventRepository.findById(eventId);
    }

    @Transactional
    public void confirmEvent(ConfirmationEventAvro confirmation) {
        String eventId = confirmation.getEventId();
        eventRepository.findById(eventId)
                .ifPresentOrElse(
                        event -> {
                            event.setStatus(EventStatus.CONFIRMED);
                            event.setConfirmedAt(confirmation.getConfirmedAt());
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

    public List<Event> getRecentEvents(int limit) {
        return eventRepository.findAll()
                .stream()
                .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
}
