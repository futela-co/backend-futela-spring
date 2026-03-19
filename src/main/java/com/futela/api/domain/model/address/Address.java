package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record Address(
        UUID id,
        String street,
        String number,
        String additionalInfo,
        Double latitude,
        Double longitude,
        UUID districtId,
        String districtName,
        UUID townId,
        String townName,
        UUID cityId,
        String cityName,
        UUID provinceId,
        String provinceName,
        UUID countryId,
        String countryName,
        Instant createdAt,
        Instant updatedAt
) {}
