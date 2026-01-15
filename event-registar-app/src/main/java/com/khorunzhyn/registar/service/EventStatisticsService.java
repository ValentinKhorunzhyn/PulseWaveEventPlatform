package com.khorunzhyn.registar.service;

import com.khorunzhyn.registar.dto.EventMetricsDto;
import com.khorunzhyn.registar.repository.RegisteredEventRepository;
import com.khorunzhyn.registar.repository.projection.EventStatisticProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventStatisticsService {

    private final RegisteredEventRepository eventRepository;

    private EventMetricsDto cachedMetrics;
    private Instant lastCacheUpdate;

    public EventMetricsDto getMetrics() {
        if (cachedMetrics == null ||
                lastCacheUpdate == null ||
                lastCacheUpdate.isBefore(Instant.now().minusSeconds(30))) {
            updateMetricsCache();
        }
        return cachedMetrics;
    }

    // Update every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void updateMetricsCache() {
        try {
            EventStatisticProjection stats = eventRepository.getStatistics();

            Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
            Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);

            long eventsLastHour = eventRepository.countByReceivedAtBetween(oneHourAgo, Instant.now());
            long eventsLast24Hours = eventRepository.countByReceivedAtBetween(oneDayAgo, Instant.now());

            long totalProcessed = stats.getProcessedEvents() + stats.getConfirmedEvents();
            double successRate = stats.getTotalEvents() > 0 ?
                    (double) totalProcessed / stats.getTotalEvents() * 100 : 0;

            cachedMetrics = new EventMetricsDto(
                    stats.getTotalEvents(),
                    totalProcessed,
                    stats.getConfirmedEvents(),
                    stats.getFailedEvents(),
                    stats.getDuplicateEvents(),
                    Math.round(successRate * 100.0) / 100.0,
                    eventsLastHour,
                    eventsLast24Hours
            );

            lastCacheUpdate = Instant.now();

        } catch (Exception e) {
            log.error("Failed to update metrics cache", e);
        }
    }

}
