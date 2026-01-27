package com.khorunzhyn.publisher.model;

import com.khorunzhyn.publisher.enums.EventType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
public abstract class AbstractEvent {
    private String eventId;
    private EventType eventType;
    private Instant timestamp;
    private String schemaVersion;
    private PublisherMetadata publisher;

    protected static abstract class AbstractEventBuilder<C extends AbstractEvent, B extends AbstractEventBuilder<C, B>> {
        public B assignDefaults(PublisherMetadata publisherMetadata) {
            this.eventId(UUID.randomUUID().toString())
                    .timestamp(Instant.now())
                    .schemaVersion("1.0")
                    .publisher(publisherMetadata);
            return self();
        }
    }
}