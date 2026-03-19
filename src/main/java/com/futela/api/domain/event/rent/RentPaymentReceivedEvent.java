package com.futela.api.domain.event.rent;

import java.math.BigDecimal;
import java.util.UUID;

public record RentPaymentReceivedEvent(
        UUID paymentId,
        UUID invoiceId,
        BigDecimal amount
) {}
