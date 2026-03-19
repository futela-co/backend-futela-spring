package com.futela.api.domain.model.rent;

import com.futela.api.domain.enums.ReminderType;

import java.time.Instant;
import java.util.UUID;

public record RentReminder(
        UUID id,
        UUID invoiceId,
        UUID leaseId,
        ReminderType type,
        Instant sentAt,
        String channel,
        UUID companyId,
        Instant createdAt
) {}
