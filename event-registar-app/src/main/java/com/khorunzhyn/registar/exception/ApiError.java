package com.khorunzhyn.registar.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Schema(description = "API error response")
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ApiError {

    @Schema(description = "HTTP status code")
    int status;

    @Schema(description = "Error type")
    String error;

    @Schema(description = "Error message")
    String message;

    @Schema(description = "Request path")
    String path;

    @Schema(description = "Timestamp when error occurred")
    Instant timestamp;

    @Schema(description = "Detailed validation errors")
    Map<String, String> details;

}
