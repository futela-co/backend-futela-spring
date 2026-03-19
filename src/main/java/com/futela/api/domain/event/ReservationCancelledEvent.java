package com.futela.api.domain.event;

import java.util.UUID;

public record ReservationCancelledEvent(
        UUID reservationId,
        UUID propertyId,
        UUID userId,
        String reason
) {}
