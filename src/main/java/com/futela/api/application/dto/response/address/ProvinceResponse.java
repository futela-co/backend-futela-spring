package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.Province;

import java.time.Instant;
import java.util.UUID;

public record ProvinceResponse(
        UUID id,
        String name,
        String code,
        boolean isActive,
        UUID countryId,
        String countryName,
        Instant createdAt
) {
    public static ProvinceResponse fromDomain(Province province) {
        return new ProvinceResponse(
                province.id(), province.name(), province.code(),
                province.isActive(), province.countryId(), province.countryName(),
                province.createdAt()
        );
    }
}
