package com.khorunzhyn.publisher.dto;

import com.khorunzhyn.publisher.model.PublisherMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    @NotNull
    private String id;
    @NotNull
    private String eventType;
    private String payload;
    @NotNull
    private String status;
    private String publisherId;
    private PublisherMetadata publisherMetadata;
    private Instant createdAt;
    private Instant confirmedAt;
    private Instant updatedAt;
    private Long version;

}
