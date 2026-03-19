package com.futela.api.infrastructure.persistence.entity.rent;

import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rent_payments", indexes = {
        @Index(name = "idx_rpayment_lease", columnList = "lease_id"),
        @Index(name = "idx_rpayment_invoice", columnList = "invoice_id")
})
@Getter
@Setter
public class RentPaymentEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private RentInvoiceEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private LeaseEntity lease;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(length = 50)
    private String paymentMethod;

    @Column
    private String reference;

    @Column(columnDefinition = "text")
    private String notes;
}
