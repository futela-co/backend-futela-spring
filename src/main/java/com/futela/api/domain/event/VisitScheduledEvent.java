package com.futela.api.domain.event;

import java.time.Instant;
import java.util.UUID;

public record VisitScheduledEvent(
        UUID visitId,
        UUID propertyId,
        UUID userId,
        Instant scheduledAt
) {}
