package com.khorunzhyn.publisher.dto;

import java.time.Instant;
import java.util.Map;

public record StatsResponseDto(long totalGenerated,
                               long totalConfirmed,
                               double confirmationRate,
                               Instant lastGenerated,
                               Instant lastConfirmed,
                               Map<String, Long> eventsByType) {
}
