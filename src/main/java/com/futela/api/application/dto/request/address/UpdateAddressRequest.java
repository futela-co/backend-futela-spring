package com.futela.api.application.dto.request.address;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAddressRequest(
        String street,
        String number,
        String additionalInfo,
        Double latitude,
        Double longitude,
        UUID districtId,

        @NotNull(message = "La commune est obligatoire")
        UUID townId
) {}
