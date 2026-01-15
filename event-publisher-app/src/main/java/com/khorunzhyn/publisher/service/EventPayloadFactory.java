package com.khorunzhyn.publisher.service;


import com.khorunzhyn.publisher.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Component
public class EventPayloadFactory {

    private static final Logger log = LoggerFactory.getLogger(EventPayloadFactory.class);

    private final ObjectMapper objectMapper;
    private final PublisherIdentityService identityService;

    public EventPayloadFactory(PublisherIdentityService identityService) {
        this.identityService = identityService;
        this.objectMapper = new ObjectMapper();
    }

    public String createPayload(EventType eventType) {
        Map<String, Object> payload = switch (eventType) {
            case USER_ACTION -> createUserActionPayload();
            case SYSTEM_ALERT -> createSystemAlertPayload();
            case BUSINESS_EVENT -> createBusinessEventPayload();
        };

        // Add common fields
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("eventType", eventType.name());
        payload.put("timestamp", Instant.now().toString());
        payload.put("schemaVersion", "1.0");
        payload.put("publisher", identityService.getMetadata());

        return objectMapper.writeValueAsString(payload);
    }

    private Map<String, Object> createUserActionPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();

        // Actor
        Map<String, Object> actor = Map.of(
                "id", "user-" + (1000 + ThreadLocalRandom.current().nextInt(9000)),
                "type", randomElement("CUSTOMER", "ADMIN", "SUPPORT", "API_CLIENT"),
                "username", "user" + ThreadLocalRandom.current().nextInt(1000) + "@example.com",
                "roles", List.of(randomElement("USER", "VIEWER", "EDITOR", "ADMIN"))
        );
        payload.put("actor", actor);

        // Action
        String action = randomElement(
                "LOGIN", "LOGOUT", "VIEW_PAGE", "CREATE_RESOURCE",
                "UPDATE_RESOURCE", "DELETE_RESOURCE", "DOWNLOAD_FILE"
        );
        payload.put("action", action);

        // Target
        Map<String, Object> target = Map.of(
                "type", randomElement("WEB_PAGE", "API_ENDPOINT", "FILE", "DATABASE_RECORD"),
                "id", "target-" + ThreadLocalRandom.current().nextInt(10000),
                "name", "/api/v1/" + action.toLowerCase().replace('_', '-')
        );
        payload.put("target", target);

        // Details
        Map<String, Object> details = Map.of(
                "success", ThreadLocalRandom.current().nextBoolean(),
                "durationMs", 50 + ThreadLocalRandom.current().nextInt(2000),
                "ipAddress", generateIpAddress(),
                "userAgent", randomUserAgent(),
                "sessionId", UUID.randomUUID().toString(),
                "location", randomElement("US", "EU", "ASIA", "UNKNOWN")
        );
        payload.put("details", details);

        return payload;
    }

    private Map<String, Object> createSystemAlertPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();

        // System component
        String component = randomElement("DATABASE", "API_GATEWAY", "CACHE", "QUEUE", "STORAGE");
        payload.put("component", component);

        // Alert details
        String alertType = randomElement(
                "HIGH_CPU", "MEMORY_LEAK", "DISK_FULL", "HIGH_LATENCY",
                "ERROR_RATE_SPIKE", "CONNECTION_POOL_EXHAUSTED"
        );
        payload.put("alertType", alertType);

        // Metrics
        Map<String, Object> metrics = Map.of(
                "cpuUsage", round(70 + ThreadLocalRandom.current().nextDouble() * 30, 1),
                "memoryUsage", round(60 + ThreadLocalRandom.current().nextDouble() * 40, 1),
                "diskUsage", round(50 + ThreadLocalRandom.current().nextDouble() * 50, 1),
                "responseTimeP95", 100 + ThreadLocalRandom.current().nextInt(900),
                "errorRate", round(ThreadLocalRandom.current().nextDouble() * 5, 2),
                "activeConnections", 10 + ThreadLocalRandom.current().nextInt(100)
        );
        payload.put("metrics", metrics);

        // Severity
        double cpu = (Double) metrics.get("cpuUsage");
        String severity = cpu > 90 ? "CRITICAL" :
                cpu > 80 ? "HIGH" :
                        cpu > 70 ? "MEDIUM" : "LOW";
        payload.put("severity", severity);

        // Context
        payload.put("message", String.format("%s detected in %s. CPU usage: %.1f%%",
                alertType.replace('_', ' '), component, cpu));
        payload.put("recommendation", randomElement(
                "Scale up instance", "Restart service", "Check dependencies",
                "Review configuration", "Increase disk space"
        ));
        payload.put("threshold", 80.0);

        return payload;
    }

    private Map<String, Object> createBusinessEventPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();

        // Business entity
        String entity = randomElement("ORDER", "PAYMENT", "CUSTOMER");
        payload.put("entity", entity);

        // Operation
        String operation = randomElement("CREATED", "UPDATED", "DELETED", "PROCESSED", "APPROVED");
        payload.put("operation", operation);

        // Business data
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("id", entity.toLowerCase() + "_" + (10000 + ThreadLocalRandom.current().nextInt(90000)));
        businessData.put("timestamp", Instant.now().minusSeconds(ThreadLocalRandom.current().nextInt(3600)).toString());

        switch (entity) {
            case "ORDER" -> {
                businessData.put("customerId", "cust_" + (1000 + ThreadLocalRandom.current().nextInt(9000)));
                businessData.put("amount", round(10 + ThreadLocalRandom.current().nextDouble() * 990, 2));
                businessData.put("currency", randomElement("USD", "EUR", "GBP", "JPY"));
                businessData.put("items", 1 + ThreadLocalRandom.current().nextInt(10));
                businessData.put("status", randomElement("PENDING", "PROCESSING", "SHIPPED", "DELIVERED"));
                businessData.put("shippingAddress", generateAddress());
            }
            case "PAYMENT" -> {
                businessData.put("orderId", "order_" + (10000 + ThreadLocalRandom.current().nextInt(90000)));
                businessData.put("amount", round(10 + ThreadLocalRandom.current().nextDouble() * 990, 2));
                businessData.put("method", randomElement("CREDIT_CARD", "PAYPAL", "BANK_TRANSFER", "CRYPTO"));
                businessData.put("successful", ThreadLocalRandom.current().nextBoolean());
                businessData.put("transactionId", "txn_" + UUID.randomUUID().toString().substring(0, 8));
            }
            case "CUSTOMER" -> {
                businessData.put("name", randomElement("John Smith", "Jane Doe", "Alex Johnson", "Maria Garcia"));
                businessData.put("email", "customer" + ThreadLocalRandom.current().nextInt(1000) + "@example.com");
                businessData.put("tier", randomElement("BASIC", "PREMIUM", "ENTERPRISE"));
                businessData.put("lifetimeValue", round(ThreadLocalRandom.current().nextDouble() * 10000, 2));
                businessData.put("signupDate", Instant.now().minus(Duration.ofDays(ThreadLocalRandom.current().nextInt(365))));
            }
        }
        payload.put("businessData", businessData);

        // Revenue impact
        if (entity.equals("ORDER") || entity.equals("PAYMENT")) {
            payload.put("revenueImpact", businessData.get("amount"));
        }

        return payload;
    }

    private String createFallbackPayload(EventType eventType) {
        return String.format("""
                {
                    "eventType": "%s",
                    "timestamp": "%s",
                    "message": "Fallback payload",
                    "publisher": %s
                }
                """, eventType.name(), Instant.now(), identityService.getPublisherId());
    }

    private String generateIpAddress() {
        return String.format("%d.%d.%d.%d",
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256)
        );
    }

    private String generateAddress() {
        return String.format("%d %s St, %s, %s %s",
                ThreadLocalRandom.current().nextInt(1000),
                randomElement("Main", "Oak", "Pine", "Maple", "Cedar"),
                randomElement("New York", "London", "Tokyo", "Berlin", "Sydney"),
                randomElement("NY", "CA", "TX", "FL", "IL"),
                (10000 + ThreadLocalRandom.current().nextInt(90000))
        );
    }

    private String randomUserAgent() {
        return randomElement(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15",
                "PostmanRuntime/7.29.0"
        );
    }

    @SafeVarargs
    private <T> T randomElement(T... elements) {
        return elements[ThreadLocalRandom.current().nextInt(elements.length)];
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
