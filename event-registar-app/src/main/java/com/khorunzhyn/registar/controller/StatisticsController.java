package com.khorunzhyn.registar.controller;

import com.khorunzhyn.registar.dto.EventMetricsDto;
import com.khorunzhyn.registar.service.EventStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class StatisticsController {

    private final EventStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Get event processing metrics")
    public ResponseEntity<EventMetricsDto> getMetrics() {
        return ResponseEntity.ok(statisticsService.getMetrics());
    }
}
