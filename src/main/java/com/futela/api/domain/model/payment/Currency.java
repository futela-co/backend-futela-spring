package com.futela.api.domain.model.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Currency(
        UUID id,
        String code,
        String name,
        String symbol,
        BigDecimal exchangeRate,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
    public BigDecimal convertAmount(BigDecimal amount) {
        return amount.multiply(exchangeRate);
    }
}
