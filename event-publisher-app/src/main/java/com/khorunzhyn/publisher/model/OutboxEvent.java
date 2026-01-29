package com.khorunzhyn.publisher.model;

import com.khorunzhyn.publisher.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String aggregateType; //"EVENT", "ORDER", "USER"

    @Column(nullable = false)
    private String aggregateId;   // eventId

    @Column(nullable = false)
    private String type;          //  "USER_ACTION", "ORDER_CREATED" and other

    @Column(columnDefinition = "JSONB", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status; // PENDING, SENT

    private Instant processedAt;
}