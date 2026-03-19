package com.futela.api.domain.event;

import java.util.UUID;

public record ReservationConfirmedEvent(
        UUID reservationId,
        UUID propertyId,
        UUID userId
) {}
