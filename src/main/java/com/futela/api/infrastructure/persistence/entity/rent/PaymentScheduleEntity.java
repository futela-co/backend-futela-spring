package com.futela.api.infrastructure.persistence.entity.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payment_schedules", indexes = {
        @Index(name = "idx_pschedule_lease", columnList = "lease_id")
})
@Getter
@Setter
public class PaymentScheduleEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private LeaseEntity lease;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "invoice_id")
    private UUID invoiceId;
}
