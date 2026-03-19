package com.futela.api.application.dto.request.reservation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationRequest(
        @NotNull(message = "L'identifiant de la propriété est requis")
        UUID propertyId,

        @NotNull(message = "La date de début est requise")
        @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
        LocalDate startDate,

        @NotNull(message = "La date de fin est requise")
        LocalDate endDate,

        @NotNull(message = "Le nombre d'invités est requis")
        @Min(value = 1, message = "Le nombre d'invités doit être d'au moins 1")
        Integer guestCount,

        String notes
) {}
