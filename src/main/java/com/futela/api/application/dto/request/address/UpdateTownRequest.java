package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;

public record UpdateTownRequest(
        @NotBlank(message = "Le nom de la commune est obligatoire")
        String name,

        String zipCode
) {}
