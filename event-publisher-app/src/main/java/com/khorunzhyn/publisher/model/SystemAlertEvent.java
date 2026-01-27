package com.khorunzhyn.publisher.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SystemAlertEvent extends AbstractEvent {
    private String component;
    private String alertType;
    private SystemMetrics metrics;
    private String severity;
    private String message;
    private String recommendation;
    private double threshold;

    @Data
    @Builder
    public static class SystemMetrics {
        private double cpuUsage;
        private double memoryUsage;
        private double diskUsage;
        private int responseTimeP95;
        private double errorRate;
        private int activeConnections;
    }
}