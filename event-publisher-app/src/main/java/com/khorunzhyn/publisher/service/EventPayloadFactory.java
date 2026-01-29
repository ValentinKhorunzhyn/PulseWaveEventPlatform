package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.enums.BusinessEntityType;
import com.khorunzhyn.publisher.enums.BusinessOperation;
import com.khorunzhyn.publisher.enums.EventType;
import com.khorunzhyn.publisher.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.khorunzhyn.publisher.util.PublisherDataUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPayloadFactory {

    private final ObjectMapper objectMapper;
    private final PublisherIdentityService identityService;

    public String createPayload(EventType eventType) {
        AbstractEvent event = createEvent(eventType);
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            log.error("Failed to serialize event", e);
            return "{}";
        }
    }

    public AbstractEvent createEvent(EventType eventType) {
        var metadata = identityService.getMetadata();

        return switch (eventType) {
            case USER_ACTION -> createUserAction(metadata);
            case SYSTEM_ALERT -> createSystemAlert(metadata);
            case AUDIT_LOG -> createAuditLog(metadata);
            case BUSINESS_EVENT -> createBusinessEvent(metadata);
        };
    }

    private UserActionEvent createUserAction(PublisherMetadata metadata) {
        String action = randomItem(USER_ACTIONS);

        return UserActionEvent.builder()
                .assignDefaults(metadata)
                .eventType(EventType.USER_ACTION)
                .actor(UserActionEvent.Actor.builder()
                        .id(randomId("user-", 1000, 9999))
                        .type(randomItem(ACTOR_TYPES))
                        .username(randomId("user", 1, 1000) + "@example.com")
                        .roles(List.of(randomItem(USER_ROLES)))
                        .build())
                .action(action)
                .target(UserActionEvent.Target.builder()
                        .type(randomItem(TARGET_TYPES))
                        .id(randomId("target-", 1, 10000))
                        .name("/api/v1/" + action.toLowerCase().replace('_', '-'))
                        .build())
                .details(UserActionEvent.ActionDetails.builder()
                        .success(randomBoolean())
                        .durationMs(randomInt(50, 2050))
                        .ipAddress(generateIpAddress())
                        .userAgent(randomItem(USER_AGENTS))
                        .sessionId(randomUuid())
                        .location(randomItem(LOCATIONS))
                        .build())
                .build();
    }

    private SystemAlertEvent createSystemAlert(PublisherMetadata metadata) {
        String component = randomItem(SYSTEM_COMPONENTS);
        String alertType = randomItem(ALERT_TYPES);

        double cpu = round(randomDouble(70, 100), 1);
        String severity = cpu > 90 ? "CRITICAL" : cpu > 80 ? "HIGH" : cpu > 70 ? "MEDIUM" : "LOW";

        return SystemAlertEvent.builder()
                .assignDefaults(metadata)
                .eventType(EventType.SYSTEM_ALERT)
                .component(component)
                .alertType(alertType)
                .metrics(SystemAlertEvent.SystemMetrics.builder()
                        .cpuUsage(cpu)
                        .memoryUsage(round(randomDouble(60, 100), 1))
                        .diskUsage(round(randomDouble(50, 100), 1))
                        .responseTimeP95(randomInt(100, 1000))
                        .errorRate(round(randomDouble(0, 5), 2))
                        .activeConnections(randomInt(10, 110))
                        .build())
                .severity(severity)
                .message(String.format("%s detected in %s. CPU usage: %.1f%%", alertType.replace('_', ' '), component, cpu))
                .recommendation(randomItem(RECOMMENDATIONS))
                .threshold(80.0)
                .build();
    }

    private AuditLogEvent createAuditLog(PublisherMetadata metadata) {
        List<AuditLogEvent.Change> changes = new ArrayList<>();
        int count = randomInt(1, 3);

        for (int i = 0; i < count; i++) {
            changes.add(AuditLogEvent.Change.builder()
                    .field(randomItem(AUDIT_FIELDS))
                    .oldValue(randomId("val_", 0, 100))
                    .newValue(randomId("val_", 0, 100))
                    .reason(randomItem(AUDIT_REASONS))
                    .build());
        }

        return AuditLogEvent.builder()
                .assignDefaults(metadata)
                .eventType(EventType.AUDIT_LOG)
                .action(randomItem(AUDIT_ACTIONS))
                .entity(AuditLogEvent.AuditEntity.builder()
                        .type(randomItem(AUDIT_ENTITY_TYPES))
                        .id(randomId("ent-", 1000, 9999))
                        .name(randomId("Entity ", 1, 1000))
                        .build())
                .changes(changes)
                .complianceStandard(randomItem(COMPLIANCE_STANDARDS))
                .requiresReview(randomBoolean())
                .auditor(randomId("auditor-", 1, 100))
                .build();
    }

    private BusinessEvent<?> createBusinessEvent(PublisherMetadata metadata) {
        var entityType = randomItem(BusinessEntityType.values());

        return switch (entityType) {
            case ORDER -> createOrderEvent(metadata);
            case PAYMENT -> createPaymentEvent(metadata);
            case CUSTOMER -> createCustomerEvent(metadata);
        };
    }

    private BusinessEvent<BusinessEvent.OrderPayload> createOrderEvent(PublisherMetadata metadata) {
        double amountVal = round(randomDouble(10, 1000), 2);

        var payload = BusinessEvent.OrderPayload.builder()
                .orderId(randomId("ord-", 10000, 99999))
                .customerId(randomId("cust-", 1000, 9999))
                .amount(BigDecimal.valueOf(amountVal))
                .currency(randomItem(CURRENCIES))
                .itemsCount(randomInt(1, 10))
                .status(randomItem(ORDER_STATUSES))
                .shippingAddress("Address " + randomInt(1, 100))
                .build();

        return BusinessEvent.<BusinessEvent.OrderPayload>builder()
                .assignDefaults(metadata)
                .eventType(EventType.BUSINESS_EVENT)
                .entityType(BusinessEntityType.ORDER)
                .businessOperation(randomItem(BusinessOperation.CREATED, BusinessOperation.UPDATED))
                .businessData(payload)
                .revenueImpact(amountVal)
                .build();
    }

    private BusinessEvent<BusinessEvent.PaymentPayload> createPaymentEvent(PublisherMetadata metadata) {
        double amountVal = round(randomDouble(10, 500), 2);
        boolean success = randomInt(0, 10) > 1;

        var payload = BusinessEvent.PaymentPayload.builder()
                .paymentId(randomId("pay-", 10000, 99999))
                .orderId(randomId("ord-", 10000, 99999))
                .amount(BigDecimal.valueOf(amountVal))
                .method(randomItem(PAYMENT_METHODS))
                .successful(success)
                .transactionId(randomUuid())
                .build();

        return BusinessEvent.<BusinessEvent.PaymentPayload>builder()
                .assignDefaults(metadata)
                .eventType(EventType.BUSINESS_EVENT)
                .entityType(BusinessEntityType.PAYMENT)
                .businessOperation(BusinessOperation.PROCESSED)
                .businessData(payload)
                .revenueImpact(success ? amountVal : 0.0)
                .build();
    }

    private BusinessEvent<BusinessEvent.CustomerPayload> createCustomerEvent(PublisherMetadata metadata) {
        var payload = BusinessEvent.CustomerPayload.builder()
                .customerId(randomId("cust-", 1000, 9999))
                .fullName(randomItem(CUSTOMER_NAMES))
                .email("user" + randomInt(1, 1000) + "@test.com")
                .tier(randomItem(CUSTOMER_TIERS))
                .signupDate(Instant.now().minus(Duration.ofDays(randomInt(0, 365))))
                .build();

        return BusinessEvent.<BusinessEvent.CustomerPayload>builder()
                .assignDefaults(metadata)
                .eventType(EventType.BUSINESS_EVENT)
                .entityType(BusinessEntityType.CUSTOMER)
                .businessOperation(randomItem(BusinessOperation.UPDATED, BusinessOperation.DELETED))
                .businessData(payload)
                .revenueImpact(null)
                .build();
    }
}