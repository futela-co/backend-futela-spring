package com.futela.api.domain.event.payment;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID transactionId,
        String externalRef
) {}
