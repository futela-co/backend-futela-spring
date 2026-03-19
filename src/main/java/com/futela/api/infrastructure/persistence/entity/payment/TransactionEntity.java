package com.futela.api.infrastructure.persistence.entity.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_reference", columnList = "reference"),
        @Index(name = "idx_tx_external_ref", columnList = "external_ref"),
        @Index(name = "idx_tx_user", columnList = "user_id"),
        @Index(name = "idx_tx_status", columnList = "status")
})
@Getter
@Setter
public class TransactionEntity extends TenantAwareEntity {

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "external_ref", unique = true)
    private String externalRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode metadata = JsonNodeFactory.instance.objectNode();

    @Column(columnDefinition = "text")
    private String failureReason;

    @Column
    private Instant processedAt;
}
