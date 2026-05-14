package com.khorunzhyn.publisher.controller;

import com.khorunzhyn.publisher.dto.EventDto;
import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.service.EventPublisherService;
import com.khorunzhyn.publisher.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Events operations API")
public class EventController {

    private final EventService eventService;
    private final EventPublisherService eventPublisherService;

    @GetMapping("/recent")
    @Operation(summary = "Get last events")
    public ResponseEntity<List<Event>> getRecentEvents(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(eventService.getRecentEvents(limit));
    }

    @PostMapping
    @Operation(summary = "Process generated event")
    public ResponseEntity<String> processEvent(@RequestBody EventDto eventDto) {
        eventPublisherService.handleEvent(eventDto);
        return ResponseEntity.ok("Event processed and saved");
    }
}
