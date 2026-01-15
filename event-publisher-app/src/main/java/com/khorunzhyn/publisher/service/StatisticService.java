package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.dto.StatsResponseDto;
import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticService {

    private final EventRepository eventRepository;

    public StatsResponseDto getStatistics() {
        List<Event> allSavedEvents = eventRepository.findAll();

        if (allSavedEvents.isEmpty()) {
            return new StatsResponseDto(0, 0, 0.0,
                    null, null, Map.of());
        }

        long totalGenerated = 0;
        long totalConfirmed = 0;
        Instant lastGenerated = null;
        Instant lastConfirmed = null;
        Map<String, Long> eventsByType = new HashMap<>();

        for (Event event : allSavedEvents) {
            totalGenerated++;

            if (event.getStatus() == EventStatus.CONFIRMED) {
                totalConfirmed++;

                if (event.getConfirmedAt() != null &&
                        (lastConfirmed == null || event.getConfirmedAt().isAfter(lastConfirmed))) {
                    lastConfirmed = event.getConfirmedAt();
                }
            }

            if (event.getCreatedAt() != null &&
                    (lastGenerated == null || event.getCreatedAt().isAfter(lastGenerated))) {
                lastGenerated = event.getCreatedAt();
            }

            String eventType = event.getEventType().name();
            eventsByType.put(eventType, eventsByType.getOrDefault(eventType, 0L) + 1);
        }

        double confirmationRate = totalGenerated > 0
                ? Math.round((double) totalConfirmed / totalGenerated * 10000) / 100.0
                : 0.0;

        return new StatsResponseDto(
                totalGenerated,
                totalConfirmed,
                confirmationRate,
                lastGenerated,
                lastConfirmed,
                eventsByType
        );
    }
}
