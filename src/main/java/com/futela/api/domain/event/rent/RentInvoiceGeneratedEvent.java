package com.futela.api.domain.event.rent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RentInvoiceGeneratedEvent(
        UUID invoiceId,
        UUID leaseId,
        BigDecimal amount,
        LocalDate dueDate
) {}
