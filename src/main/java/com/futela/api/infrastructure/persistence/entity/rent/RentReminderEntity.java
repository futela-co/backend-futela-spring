package com.futela.api.infrastructure.persistence.entity.rent;

import com.futela.api.domain.enums.ReminderType;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "rent_reminders", indexes = {
        @Index(name = "idx_rreminder_lease", columnList = "lease_id"),
        @Index(name = "idx_rreminder_invoice", columnList = "invoice_id")
})
@Getter
@Setter
public class RentReminderEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private RentInvoiceEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private LeaseEntity lease;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    @Column(nullable = false)
    private Instant sentAt;

    @Column(nullable = false, length = 50)
    private String channel;
}
