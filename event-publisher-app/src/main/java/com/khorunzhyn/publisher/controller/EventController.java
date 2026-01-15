package com.khorunzhyn.publisher.controller;

import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Events operations API")
public class EventController {

    private final EventService eventService;

    @GetMapping("/recent")
    @Operation(summary = "Get last events")
    public ResponseEntity<List<Event>> getRecentEvents(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(eventService.getRecentEvents(limit));
    }

}
