package com.futela.api.domain.event;

import java.util.UUID;

public record ReservationCreatedEvent(
        UUID reservationId,
        UUID propertyId,
        UUID userId,
        UUID hostId
) {}
