package com.futela.api.domain.event.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentInitiatedEvent(
        UUID transactionId,
        BigDecimal amount,
        String currency,
        UUID userId
) {}
