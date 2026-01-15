package com.khorunzhyn.publisher.controller;

import com.khorunzhyn.publisher.dto.StatsResponseDto;
import com.khorunzhyn.publisher.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API for statistic by publishers")
public class StatisticsController {

    private final StatisticService statisticService;

    @GetMapping
    @Operation(summary = "Get all statistics by events")
    public ResponseEntity<StatsResponseDto> getStats() {
        StatsResponseDto stats = statisticService.getStatistics();
        return ResponseEntity.ok(stats);
    }

}
