package com.khorunzhyn.publisher.repository;

import com.khorunzhyn.publisher.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    Optional<OutboxEvent> findByAggregateId(String eventId);
}
