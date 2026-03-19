package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCityRequest(
        @NotBlank(message = "Le nom de la ville est obligatoire")
        String name,

        String zipCode,

        @NotNull(message = "La province est obligatoire")
        UUID provinceId
) {}
