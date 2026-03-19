package com.futela.api.domain.event.payment;

import java.util.UUID;

public record PaymentRefundedEvent(
        UUID originalTransactionId,
        UUID refundTransactionId
) {}
