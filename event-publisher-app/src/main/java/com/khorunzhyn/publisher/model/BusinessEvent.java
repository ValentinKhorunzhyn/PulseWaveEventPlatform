package com.khorunzhyn.publisher.model;

import com.khorunzhyn.publisher.enums.BusinessEntityType;
import com.khorunzhyn.publisher.enums.BusinessOperation;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BusinessEvent<T> extends AbstractEvent {

    private BusinessEntityType entityType;
    private BusinessOperation businessOperation;
    private T businessData;
    private Double revenueImpact;

    @Data
    @Builder
    public static class OrderPayload {
        private String orderId;
        private String customerId;
        private BigDecimal amount;
        private String currency;
        private int itemsCount;
        private String status;
        private String shippingAddress;
    }

    @Data
    @Builder
    public static class PaymentPayload {
        private String paymentId;
        private String orderId;
        private BigDecimal amount;
        private String method; // CREDIT_CARD, PAYPAL
        private boolean successful;
        private String transactionId;
    }

    @Data
    @Builder
    public static class CustomerPayload {
        private String customerId;
        private String fullName;
        private String email;
        private String tier; // BASIC, PREMIUM
        private Instant signupDate;
    }
}