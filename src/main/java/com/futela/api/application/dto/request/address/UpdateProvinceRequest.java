package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;

public record UpdateProvinceRequest(
        @NotBlank(message = "Le nom de la province est obligatoire")
        String name,

        String code
) {}
