package com.futela.api.domain.event;

import java.util.UUID;

public record ReviewCreatedEvent(
        UUID reviewId,
        UUID propertyId,
        UUID userId,
        int rating
) {}
