package com.futela.api.infrastructure.persistence.entity.reservation;

import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "visits", indexes = {
        @Index(name = "idx_visit_property", columnList = "property_id"),
        @Index(name = "idx_visit_user", columnList = "user_id"),
        @Index(name = "idx_visit_status", columnList = "status")
})
@Getter
@Setter
public class VisitEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status = VisitStatus.SCHEDULED;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "payment_transaction_id")
    private String paymentTransactionId;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;
}
