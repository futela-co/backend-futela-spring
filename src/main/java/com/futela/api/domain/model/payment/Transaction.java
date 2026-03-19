package com.futela.api.domain.model.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record Transaction(
        UUID id,
        String reference,
        String externalRef,
        TransactionType type,
        TransactionStatus status,
        BigDecimal amount,
        String currency,
        String phoneNumber,
        String provider,
        UUID userId,
        String userName,
        String description,
        Map<String, Object> metadata,
        String failureReason,
        Instant processedAt,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {
    public boolean isFinal() {
        return status == TransactionStatus.COMPLETED
                || status == TransactionStatus.FAILED
                || status == TransactionStatus.CANCELLED;
    }
}
