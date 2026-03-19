package com.futela.api.domain.event.rent;

import java.time.LocalDate;
import java.util.UUID;

public record LeaseRenewedEvent(
        UUID leaseId,
        LocalDate newEndDate
) {}
