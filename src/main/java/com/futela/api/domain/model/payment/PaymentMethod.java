package com.futela.api.domain.model.payment;

import java.time.Instant;
import java.util.UUID;

public record PaymentMethod(
        UUID id,
        String name,
        String code,
        String provider,
        boolean isActive,
        String logo,
        Instant createdAt
) {}
