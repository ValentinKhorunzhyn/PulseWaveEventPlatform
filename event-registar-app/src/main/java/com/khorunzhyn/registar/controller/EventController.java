package com.khorunzhyn.registar.controller;

import com.khorunzhyn.registar.dto.EventFilterRequestDto;
import com.khorunzhyn.registar.dto.EventResponseDto;
import com.khorunzhyn.registar.dto.PageResponse;
import com.khorunzhyn.registar.mapper.EventMapper;
import com.khorunzhyn.registar.mapper.PageMapper;
import com.khorunzhyn.registar.service.EventRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event registration and management API")
public class EventController {

    private final EventRegistrationService eventService;
    private final EventMapper eventMapper;
    private final PageMapper pageMapper;

    @GetMapping
    @Operation(
            summary = "Get events with filtering and pagination",
            description = "Retrieve registered events with various filters and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events")
    @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    public ResponseEntity<PageResponse<EventResponseDto>> getEvents(
            @ParameterObject @Valid EventFilterRequestDto filterRequest) {

        log.debug("Getting events with filters: {}", filterRequest);

        Page<EventResponseDto> eventsPage = eventService.findEvents(filterRequest)
                .map(eventMapper::toResponse);

        return ResponseEntity.ok(pageMapper.toPageResponse(eventsPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    @ApiResponse(responseCode = "200", description = "Event found")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventResponseDto> getEventById(
            @Parameter(name = "id", description = "Event ID", required = true)
            @PathVariable("id") UUID id) {

        return eventService.findEventById(id)
                .map(eventMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-event-id/{eventId}")
    @Operation(summary = "Get event by original event ID")
    public ResponseEntity<EventResponseDto> getEventByEventId(
            @Parameter(name = "eventId", description = "Original event ID from publisher", required = true)
            @PathVariable("eventId") String eventId) {

        return eventService.findEventByEventId(eventId)
                .map(eventMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
