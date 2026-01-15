package com.khorunzhyn.registar.repository.specification;

import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

@Getter
@Builder
public class EventSearchCriteria {
    private String search;
    private Set<String> sourcePublisherIds;
    private Set<EventType> eventTypes;
    private Set<EventStatus> processingStatuses;
    private Instant fromDate;
    private Instant toDate;
}
