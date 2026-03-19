package com.futela.api.domain.model.rent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RentPayment(
        UUID id,
        UUID invoiceId,
        UUID leaseId,
        BigDecimal amount,
        LocalDate paymentDate,
        String paymentMethod,
        String reference,
        String notes,
        UUID companyId,
        Instant createdAt
) {}
