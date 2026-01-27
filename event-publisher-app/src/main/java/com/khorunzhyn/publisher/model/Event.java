package com.khorunzhyn.publisher.model;


import com.khorunzhyn.publisher.enums.EventStatus;
import com.khorunzhyn.publisher.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "table_events")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @UuidGenerator
    private String id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Event type is required")
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Event status can't be empty")
    private EventStatus status;

    private String publisherId;

    @Column(columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private PublisherMetadata publisherMetadata; // JSON with metadata

    @CreationTimestamp
    private Instant createdAt;

    private Instant confirmedAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", eventType=" + eventType +
                ", payload='" + payload + '\'' +
                ", status=" + status +
                ", publisherId='" + publisherId + '\'' +
                ", publisherMetadata='" + publisherMetadata + '\'' +
                ", createdAt=" + createdAt +
                ", confirmedAt=" + confirmedAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }

}
