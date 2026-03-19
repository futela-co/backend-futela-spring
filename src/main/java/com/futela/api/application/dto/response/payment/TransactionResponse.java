package com.futela.api.application.dto.response.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.model.payment.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
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
        String failureReason,
        Instant processedAt,
        Instant createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.id(),
                tx.reference(),
                tx.externalRef(),
                tx.type(),
                tx.status(),
                tx.amount(),
                tx.currency(),
                tx.phoneNumber(),
                tx.provider(),
                tx.userId(),
                tx.userName(),
                tx.description(),
                tx.failureReason(),
                tx.processedAt(),
                tx.createdAt()
        );
    }
}
