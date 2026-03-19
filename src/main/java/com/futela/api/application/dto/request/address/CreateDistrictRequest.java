package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateDistrictRequest(
        @NotBlank(message = "Le nom du quartier est obligatoire")
        String name,

        UUID cityId,

        UUID townId
) {}
