package com.futela.api.domain.model.rent;

import com.futela.api.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentSchedule(
        UUID id,
        UUID leaseId,
        LocalDate dueDate,
        BigDecimal amount,
        PaymentStatus status,
        UUID invoiceId,
        UUID companyId,
        Instant createdAt
) {}
