package com.khorunzhyn.publisher.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AuditLogEvent extends AbstractEvent {
    private String action;
    private AuditEntity entity;
    private List<Change> changes;
    private String complianceStandard;
    private boolean requiresReview;
    private String auditor;

    @Data
    @Builder
    public static class AuditEntity {
        private String type;
        private String id;
        private String name;
    }

    @Data
    @Builder
    public static class Change {
        private String field;
        private String oldValue;
        private String newValue;
        private String reason;
    }
}