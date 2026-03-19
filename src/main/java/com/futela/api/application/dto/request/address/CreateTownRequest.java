package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTownRequest(
        @NotBlank(message = "Le nom de la commune est obligatoire")
        String name,

        String zipCode,

        @NotNull(message = "La ville est obligatoire")
        UUID cityId
) {}
