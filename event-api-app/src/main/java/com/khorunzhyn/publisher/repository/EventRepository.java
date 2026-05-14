package com.khorunzhyn.publisher.repository;

import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {

    long countByStatus(EventStatus status);

    long countByPublisherId(String publisherId);

    long countByPublisherIdAndStatus(String publisherId, EventStatus status);

    Optional<Event> findTopByOrderByCreatedAtDesc();

    Optional<Event> findTopByStatusOrderByConfirmedAtDesc(EventStatus status);

    List<Event> findByStatusAndCreatedAtBefore(
            EventStatus status,
            Instant createdAt
    );
}
