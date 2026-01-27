package com.khorunzhyn.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherMetadata {
    private String id;
    private String name;
    private String instanceId;
    private String hostname;
    private String containerId;
    private String javaVersion;
    private String startTime; // Можно использовать Instant, но String проще для JSON
}