package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.enums.EventType;
import com.khorunzhyn.publisher.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventPayloadFactory payloadFactory;
    private final PublisherIdentityService identityService;

    public Event generateEvent() {

        EventType eventType = EventType.getRandomEventType();
        String payload = payloadFactory.createPayload(eventType);

        return Event.builder()
                .eventType(eventType)
                .payload(payload)
                .status(EventStatus.CREATED)
                .publisherId(identityService.getPublisherId())
                .publisherMetadata(identityService.getMetadata())
                .createdAt(Instant.now())
                .build();
    }
}
