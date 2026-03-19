package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;

public record UpdateCityRequest(
        @NotBlank(message = "Le nom de la ville est obligatoire")
        String name,

        String zipCode
) {}
