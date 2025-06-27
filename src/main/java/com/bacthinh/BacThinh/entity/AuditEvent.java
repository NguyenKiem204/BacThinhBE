package com.bacthinh.BacThinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy; // Thêm import này
import org.springframework.data.annotation.CreatedDate; // Thêm import này
import org.springframework.data.annotation.LastModifiedBy; // Thêm import này
import org.springframework.data.annotation.LastModifiedDate; // Thêm import này
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Thêm import này

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_entity_id", columnList = "entityId"),
        @Index(name = "idx_audit_entity_type", columnList = "entityType"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "action", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "changed_fields", columnDefinition = "TEXT")
    private String changedFields;

    @Column(name = "timestamp", nullable = false)
    private java.time.LocalDateTime timestamp;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    // Nếu bạn muốn giữ các trường audit này cho AuditEvent, hãy thêm chúng vào đây
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    private Long version;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "client_ip", length = 50)
    private String clientIp;
}