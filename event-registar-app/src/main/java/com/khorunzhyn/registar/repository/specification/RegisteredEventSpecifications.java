package com.khorunzhyn.registar.repository.specification;

import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import com.khorunzhyn.registar.model.RegisteredEvent;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RegisteredEventSpecifications {
    public static Specification<RegisteredEvent> withCriteria(EventSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search in payload or metadata (JSON search)
            if (StringUtils.hasText(criteria.getSearch())) {
                String searchPattern = "%" + criteria.getSearch().toLowerCase() + "%";
                Predicate payloadSearch = cb.like(
                        cb.lower(root.get("payload")),
                        searchPattern
                );
                predicates.add(payloadSearch);
            }

            // Filter by source publisher IDs
            if (criteria.getSourcePublisherIds() != null && !criteria.getSourcePublisherIds().isEmpty()) {
                predicates.add(root.get("sourcePublisherId").in(criteria.getSourcePublisherIds()));
            }

            // Filter by event types
            if (criteria.getEventTypes() != null && !criteria.getEventTypes().isEmpty()) {
                predicates.add(root.get("eventType").in(criteria.getEventTypes()));
            }

            // Filter by processing status
            if (criteria.getProcessingStatuses() != null && !criteria.getProcessingStatuses().isEmpty()) {
                predicates.add(root.get("processingStatus").in(criteria.getProcessingStatuses()));
            }

            // Filter by date range
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("receivedAt"), criteria.getFromDate()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("receivedAt"), criteria.getToDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<RegisteredEvent> byPublisherId(String publisherId) {
        return (root, query, cb) ->
                cb.equal(root.get("sourcePublisherId"), publisherId);
    }

    public static Specification<RegisteredEvent> byEventType(EventType eventType) {
        return (root, query, cb) ->
                cb.equal(root.get("eventType"), eventType);
    }

    public static Specification<RegisteredEvent> byStatus(EventStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("eventStatus"), status);
    }

    public static Specification<RegisteredEvent> createdBetween(Instant from, Instant to) {
        return (root, query, cb) ->
                cb.between(root.get("receivedAt"), from, to);
    }
}
