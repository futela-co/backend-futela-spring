package com.futela.api.application.dto.response.rent;

import com.futela.api.domain.enums.ReminderType;
import com.futela.api.domain.model.rent.RentReminder;

import java.time.Instant;
import java.util.UUID;

public record RentReminderResponse(
        UUID id,
        UUID invoiceId,
        UUID leaseId,
        ReminderType type,
        Instant sentAt,
        String channel,
        Instant createdAt
) {
    public static RentReminderResponse from(RentReminder reminder) {
        return new RentReminderResponse(
                reminder.id(),
                reminder.invoiceId(),
                reminder.leaseId(),
                reminder.type(),
                reminder.sentAt(),
                reminder.channel(),
                reminder.createdAt()
        );
    }
}
