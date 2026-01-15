package com.khorunzhyn.registar.model;

import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "table_reg_events")
@EntityListeners(AuditingEntityListener.class)
public class RegisteredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String eventId; // Original event ID from publisher

    @Column(nullable = false)
    private String sourcePublisherId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Builder.Default
    @Column(columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> publisherMetadata = new HashMap<>();

    @Column(nullable = false)
    private Instant originalCreatedAt; // When event was created at generator

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant receivedAt; // When event was received by registration service

    @Column
    private Instant processedAt;

    @Column
    private Instant confirmedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus processingStatus;

    @Column(length = 1000)
    private String errorMessage;

    @Column
    private String confirmationId; // ID of confirmation sent back

    @Version
    private Long version;

    @LastModifiedDate
    private Instant updatedAt;

    public boolean isProcessed() {
        return processingStatus == EventStatus.PROCESSED
                || processingStatus == EventStatus.CONFIRMED;
    }

    public boolean isFailed() {
        return processingStatus == EventStatus.FAILED;
    }

    public boolean isDuplicate() {
        return processingStatus == EventStatus.DUPLICATE;
    }

    public void markAsProcessed() {
        this.processingStatus = EventStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    public void markAsConfirmed(String confirmationId) {
        this.processingStatus = EventStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        this.confirmationId = confirmationId;
    }

    public void markAsFailed(String errorMessage) {
        this.processingStatus = EventStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = Instant.now();
    }

    public void markAsDuplicate() {
        this.processingStatus = EventStatus.DUPLICATE;
        this.processedAt = Instant.now();
    }
}


