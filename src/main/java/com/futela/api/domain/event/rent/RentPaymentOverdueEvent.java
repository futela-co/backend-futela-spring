package com.futela.api.domain.event.rent;

import java.util.UUID;

public record RentPaymentOverdueEvent(
        UUID invoiceId,
        long daysOverdue
) {}
