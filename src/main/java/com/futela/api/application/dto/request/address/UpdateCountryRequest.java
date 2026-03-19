package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;

public record UpdateCountryRequest(
        @NotBlank(message = "Le nom du pays est obligatoire")
        String name,

        String phoneCode
) {}
