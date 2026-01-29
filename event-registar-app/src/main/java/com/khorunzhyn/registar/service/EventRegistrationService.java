package com.khorunzhyn.registar.service;


import ch.qos.logback.core.util.StringUtil;
import com.khorunzhyn.registar.dto.EventFilterRequestDto;
import com.khorunzhyn.registar.dto.EventMessageDto;
import com.khorunzhyn.registar.mapper.EventMapper;
import com.khorunzhyn.registar.model.RegisteredEvent;
import com.khorunzhyn.registar.repository.RegisteredEventRepository;
import com.khorunzhyn.registar.repository.specification.EventSearchCriteria;
import com.khorunzhyn.registar.repository.specification.RegisteredEventSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final RegisteredEventRepository eventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EventMapper eventMapper;

    @Transactional
    public RegisteredEvent registerEvent(EventMessageDto message) {
        log.info("Registering event: {} from generator: {}",
                message.eventId(), message.publisherId());

        // Check for duplicates
        Optional<RegisteredEvent> existingEvent = eventRepository.findByEventId(message.eventId());
        if (existingEvent.isPresent()) {
            RegisteredEvent event = existingEvent.get();
            log.warn("Duplicate event detected: {}", message.eventId());
            return event;
        }

        // Create new event
        RegisteredEvent event = eventMapper.toEntity(message);
        RegisteredEvent savedEvent = eventRepository.save(event);

        // Process the event
        try {
            processEvent(savedEvent);
        } catch (Exception e) {
            log.error("Failed to process event {}: {}", savedEvent.getEventId(), e.getMessage(), e);
            savedEvent.markAsFailed(e.getMessage());
            eventRepository.save(savedEvent);
            throw e;
        }

        return savedEvent;
    }

    private void processEvent(RegisteredEvent event) {
        // Validate payload
        validatePayload(event.getPayload());
        // Process business logic
        event.markAsProcessed();
        // Send confirmation
        String confirmationId = UUID.randomUUID().toString();
        event.markAsConfirmed(confirmationId);
        eventRepository.save(event);

        log.info("Event {} processed and confirmed. Confirmation ID: {}",
                event.getEventId(), confirmationId);
    }

    @Transactional(readOnly = true)
    public Page<RegisteredEvent> findEvents(EventFilterRequestDto filterRequest) {
        EventSearchCriteria criteria = EventSearchCriteria.builder()
                .search(filterRequest.search())
                .sourcePublisherIds(filterRequest.sourcePublisherIds())
                .eventTypes(filterRequest.eventTypes())
                .processingStatuses(filterRequest.processingStatuses())
                .fromDate(filterRequest.fromDate())
                .toDate(filterRequest.toDate())
                .build();

        Specification<RegisteredEvent> spec = RegisteredEventSpecifications.withCriteria(criteria);

        Sort sort = Sort.by(
                filterRequest.sortDirection().equalsIgnoreCase("DESC") ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                filterRequest.sortBy()
        );

        Pageable pageable = PageRequest.of(filterRequest.page(), filterRequest.size(), sort);

        return eventRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<RegisteredEvent> findEventById(UUID id) {
        return eventRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RegisteredEvent> findEventByEventId(String eventId) {
        return eventRepository.findByEventId(eventId);
    }

    public void reprocessFailedEvent(UUID id) {
        eventRepository.findById(id)
                .filter(event -> event.isFailed())
                .ifPresent(event -> {
                    log.info("Reprocessing failed event: {}", event.getEventId());
                    try {
                        processEvent(event);
                    } catch (Exception e) {
                        log.error("Failed to reprocess event {}: {}", event.getEventId(), e.getMessage());
                        event.markAsFailed("Reprocessing failed: " + e.getMessage());
                        eventRepository.save(event);
                    }
                });
    }

    private void validatePayload(String payload) {
        // Basic validation for test app
        if (StringUtil.isNullOrEmpty(payload)) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }

        // Additional validation logic can be added here
    }

}
