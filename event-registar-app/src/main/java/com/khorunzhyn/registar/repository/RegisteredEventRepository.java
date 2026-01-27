package com.khorunzhyn.registar.repository;


import com.khorunzhyn.registar.model.RegisteredEvent;
import com.khorunzhyn.registar.repository.projection.EventStatisticProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RegisteredEventRepository extends JpaRepository<RegisteredEvent, UUID>,
        JpaSpecificationExecutor<RegisteredEvent> {

    Optional<RegisteredEvent> findByEventId(String eventId);

    long countByReceivedAtBetween(Instant start, Instant end);

    @Query("""
                SELECT 
                    COUNT(e) as totalEvents,
                    COALESCE(SUM(CASE WHEN e.processingStatus = 'PROCESSED' THEN 1 ELSE 0 END), 0) as processedEvents,
                    COALESCE(SUM(CASE WHEN e.processingStatus = 'CONFIRMED' THEN 1 ELSE 0 END), 0) as confirmedEvents,
                    COALESCE(SUM(CASE WHEN e.processingStatus = 'FAILED' THEN 1 ELSE 0 END), 0) as failedEvents,
                    COALESCE(SUM(CASE WHEN e.processingStatus = 'DUPLICATE' THEN 1 ELSE 0 END), 0) as duplicateEvents
                FROM RegisteredEvent e
            """)
    EventStatisticProjection getStatistics();

}
