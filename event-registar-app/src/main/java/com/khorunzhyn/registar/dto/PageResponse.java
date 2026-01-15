package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response")
public record PageResponse<T>(
        @Schema(description = "List of items")
        @JsonProperty("content")
        List<T> content,

        @Schema(description = "Current page number")
        @JsonProperty("page")
        int page,

        @Schema(description = "Page size")
        @JsonProperty("size")
        int size,

        @Schema(description = "Total number of elements")
        @JsonProperty("totalElements")
        long totalElements,

        @Schema(description = "Total number of pages")
        @JsonProperty("totalPages")
        int totalPages
) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }
}
