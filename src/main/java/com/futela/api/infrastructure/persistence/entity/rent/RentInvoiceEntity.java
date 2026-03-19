package com.futela.api.infrastructure.persistence.entity.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rent_invoices", indexes = {
        @Index(name = "idx_rinvoice_lease", columnList = "lease_id"),
        @Index(name = "idx_rinvoice_status", columnList = "status"),
        @Index(name = "idx_rinvoice_due_date", columnList = "due_date")
})
@Getter
@Setter
public class RentInvoiceEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private LeaseEntity lease;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column(precision = 12, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;
}
