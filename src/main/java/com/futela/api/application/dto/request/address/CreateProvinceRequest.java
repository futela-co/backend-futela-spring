package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateProvinceRequest(
        @NotBlank(message = "Le nom de la province est obligatoire")
        String name,

        String code,

        @NotNull(message = "Le pays est obligatoire")
        UUID countryId
) {}
