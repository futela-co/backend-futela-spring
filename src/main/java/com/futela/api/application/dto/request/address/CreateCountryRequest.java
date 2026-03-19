package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCountryRequest(
        @NotBlank(message = "Le nom du pays est obligatoire")
        String name,

        @NotBlank(message = "Le code ISO est obligatoire")
        @Size(min = 2, max = 2, message = "Le code ISO doit contenir exactement 2 caractères")
        String code,

        String phoneCode
) {}
