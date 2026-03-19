package com.futela.api.domain.event.rent;

import java.util.UUID;

public record LeaseCreatedEvent(
        UUID leaseId,
        UUID propertyId,
        UUID tenantId,
        UUID landlordId
) {}
