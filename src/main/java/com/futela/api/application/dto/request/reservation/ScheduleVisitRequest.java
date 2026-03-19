package com.futela.api.application.dto.request.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ScheduleVisitRequest(
        @NotNull(message = "L'identifiant de la propriété est requis")
        UUID propertyId,

        @NotNull(message = "La date de visite est requise")
        @Future(message = "La date de visite doit être dans le futur")
        Instant scheduledAt,

        String notes
) {}
