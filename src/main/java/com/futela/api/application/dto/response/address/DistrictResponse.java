package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.District;

import java.time.Instant;
import java.util.UUID;

public record DistrictResponse(
        UUID id,
        String name,
        boolean isActive,
        UUID cityId,
        String cityName,
        UUID townId,
        String townName,
        Instant createdAt
) {
    public static DistrictResponse fromDomain(District district) {
        return new DistrictResponse(
                district.id(), district.name(), district.isActive(),
                district.cityId(), district.cityName(),
                district.townId(), district.townName(),
                district.createdAt()
        );
    }
}
