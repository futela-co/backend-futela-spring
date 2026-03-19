package com.futela.api.domain.event;

import java.util.UUID;

public record VisitConfirmedEvent(
        UUID visitId,
        UUID propertyId,
        UUID userId
) {}
