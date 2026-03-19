package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.City;

import java.time.Instant;
import java.util.UUID;

public record CityResponse(
        UUID id,
        String name,
        String zipCode,
        boolean isActive,
        UUID provinceId,
        String provinceName,
        Instant createdAt
) {
    public static CityResponse fromDomain(City city) {
        return new CityResponse(
                city.id(), city.name(), city.zipCode(),
                city.isActive(), city.provinceId(), city.provinceName(),
                city.createdAt()
        );
    }
}
