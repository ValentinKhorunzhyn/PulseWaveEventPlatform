package com.khorunzhyn.publisher.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserActionEvent extends AbstractEvent {
    private Actor actor;
    private String action;
    private Target target;
    private ActionDetails details;

    @Data
    @Builder
    public static class Actor {
        private String id;
        private String type;
        private String username;
        private List<String> roles;
    }

    @Data
    @Builder
    public static class Target {
        private String type;
        private String id;
        private String name;
    }

    @Data
    @Builder
    public static class ActionDetails {
        private boolean success;
        private long durationMs;
        private String ipAddress;
        private String userAgent;
        private String sessionId;
        private String location;
    }
}