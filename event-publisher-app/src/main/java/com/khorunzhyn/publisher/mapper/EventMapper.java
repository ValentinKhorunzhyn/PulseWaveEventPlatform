package com.khorunzhyn.publisher.mapper;

import com.khorunzhyn.publisher.dto.EventMessageDto;
import com.khorunzhyn.publisher.model.Event;

public class EventMapper {

    public static EventMessageDto totEventMessageDto(Event event) {
        return new EventMessageDto(
                event.getId(),
                event.getPublisherId(),
                event.getPublisherMetadata(),
                event.getEventType(),
                event.getPayload(),
                event.getCreatedAt()
        );
    }

}
