package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.Town;

import java.time.Instant;
import java.util.UUID;

public record TownResponse(
        UUID id,
        String name,
        String zipCode,
        boolean isActive,
        UUID cityId,
        String cityName,
        Instant createdAt
) {
    public static TownResponse fromDomain(Town town) {
        return new TownResponse(
                town.id(), town.name(), town.zipCode(),
                town.isActive(), town.cityId(), town.cityName(),
                town.createdAt()
        );
    }
}
