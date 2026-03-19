package com.futela.api.domain.event.rent;

import java.util.UUID;

public record LeaseTerminatedEvent(
        UUID leaseId,
        String reason
) {}
