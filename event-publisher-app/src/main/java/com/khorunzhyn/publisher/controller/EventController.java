package com.khorunzhyn.publisher.controller;

import com.khorunzhyn.publisher.model.Event;
import com.khorunzhyn.publisher.service.EventPublisherService;
import com.khorunzhyn.publisher.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/generate")
    @Operation(summary = "Generate scheduled event")
    public ResponseEntity<Void> generateEvent() {
        log.info("Generate event from local server for testing");

        log.info("Checking trace context...");
        var mdcContent = MDC.getCopyOfContextMap();
        log.info("MDC Content: " + mdcContent);

        eventPublisherService.generateEvent();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
