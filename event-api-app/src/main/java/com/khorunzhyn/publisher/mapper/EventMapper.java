package com.khorunzhyn.publisher.mapper;

import com.khorunzhyn.publisher.dto.EventDto;
import com.khorunzhyn.publisher.dto.EventMessageDto;
import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.enums.EventType;
import com.khorunzhyn.publisher.model.Event;

public class EventMapper {

    public static EventMessageDto toEventMessageDto(Event event) {
        return new EventMessageDto(
                event.getId(),
                event.getPublisherId(),
                event.getPublisherMetadata(),
                event.getEventType(),
                event.getPayload(),
                event.getCreatedAt()
        );
    }

    public static Event toEvent(EventDto eventDto) {
        return new Event(
                eventDto.getId(),
                EventType.valueOf(eventDto.getEventType()),
                eventDto.getPayload(),
                EventStatus.valueOf(eventDto.getStatus()),
                eventDto.getPublisherId(),
                eventDto.getPublisherMetadata(),
                eventDto.getCreatedAt(),
                eventDto.getConfirmedAt(),
                eventDto.getUpdatedAt(),
                eventDto.getVersion()
        );
    }

}
