package com.futela.api.domain.event;

import java.util.UUID;

public record ReviewFlaggedEvent(
        UUID reviewId,
        UUID propertyId,
        String reason
) {}
