package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Set;

@Schema(description = "Event filter request")
public record EventFilterRequestDto(
        @Schema(description = "Search text in payload or metadata")
        String search,

        @Schema(description = "Source publisher IDs")
        Set<String> sourcePublisherIds,

        @Schema(description = "Event types")
        Set<EventType> eventTypes,

        @Schema(description = "Processing statuses")
        Set<EventStatus> processingStatuses,

        @Schema(description = "Start date for filtering")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        Instant fromDate,

        @Schema(description = "End date for filtering")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        Instant toDate,

        @Schema(description = "Page number", defaultValue = "0")
        @Min(0)
        Integer page,

        @Schema(description = "Page size", defaultValue = "20")
        @Min(1)
        Integer size,

        @Schema(description = "Sort field", defaultValue = "receivedAt")
        String sortBy,

        @Schema(description = "Sort direction", defaultValue = "DESC")
        String sortDirection
) {
    public EventFilterRequestDto {
        if (page == null) page = 0;
        if (size == null) size = 20;
        if (sortBy == null) sortBy = "receivedAt";
        if (sortDirection == null) sortDirection = "DESC";
    }
}
