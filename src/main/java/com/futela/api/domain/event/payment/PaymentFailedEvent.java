package com.futela.api.domain.event.payment;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID transactionId,
        String failureReason
) {}
