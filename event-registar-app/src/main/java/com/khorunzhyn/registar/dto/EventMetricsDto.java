package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Event processing metrics")
public record EventMetricsDto(
        @Schema(description = "Total events received")
        @JsonProperty("totalReceived")
        long totalReceived,

        @Schema(description = "Total events processed")
        @JsonProperty("totalProcessed")
        long totalProcessed,

        @Schema(description = "Total events confirmed")
        @JsonProperty("totalConfirmed")
        long totalConfirmed,

        @Schema(description = "Total failed events")
        @JsonProperty("totalFailed")
        long totalFailed,

        @Schema(description = "Total duplicate events")
        @JsonProperty("totalDuplicates")
        long totalDuplicates,

        @Schema(description = "Processing success rate")
        @JsonProperty("successRate")
        double successRate,

        @Schema(description = "Events received in last hour")
        @JsonProperty("eventsLastHour")
        long eventsLastHour,

        @Schema(description = "Events received in last 24 hours")
        @JsonProperty("eventsLast24Hours")
        long eventsLast24Hours
) {
}
