package com.khorunzhyn.publisher.dto;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.khorunzhyn.publisher.enums.EventType;
import com.khorunzhyn.publisher.model.PublisherMetadata;

import java.time.Instant;

public record EventMessageDto(
        String eventId,
        String publisherId,
        PublisherMetadata publisherMetadata,
        EventType eventType,
        String payload,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant createdAt) {

    public EventMessageDto {
        if (StringUtil.isNullOrEmpty(eventId)) {
            throw new IllegalArgumentException("eventId can't be null or empty");
        }
        if (StringUtil.isNullOrEmpty(publisherId)) {
            throw new IllegalArgumentException("publisherId can't be null or empty");
        }
    }
}
