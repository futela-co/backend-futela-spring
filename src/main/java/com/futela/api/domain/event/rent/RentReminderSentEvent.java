package com.futela.api.domain.event.rent;

import com.futela.api.domain.enums.ReminderType;

import java.util.UUID;

public record RentReminderSentEvent(
        UUID reminderId,
        ReminderType type,
        String channel
) {}
