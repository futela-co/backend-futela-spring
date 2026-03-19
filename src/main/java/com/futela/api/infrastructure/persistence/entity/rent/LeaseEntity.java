package com.futela.api.infrastructure.persistence.entity.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "leases", indexes = {
        @Index(name = "idx_lease_landlord", columnList = "landlord_id"),
        @Index(name = "idx_lease_tenant", columnList = "tenant_id"),
        @Index(name = "idx_lease_property", columnList = "property_id"),
        @Index(name = "idx_lease_status", columnList = "status")
})
@Getter
@Setter
public class LeaseEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private UserEntity tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private UserEntity landlord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaseStatus status = LeaseStatus.ACTIVE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal depositAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int paymentDayOfMonth;

    @Column(columnDefinition = "text")
    private String notes;

    @Column
    private Instant terminatedAt;

    @Column(columnDefinition = "text")
    private String terminationReason;
}
