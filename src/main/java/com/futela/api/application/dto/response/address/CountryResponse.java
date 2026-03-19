package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.Country;

import java.time.Instant;
import java.util.UUID;

public record CountryResponse(
        UUID id,
        String name,
        String code,
        String phoneCode,
        boolean isActive,
        Instant createdAt
) {
    public static CountryResponse fromDomain(Country country) {
        return new CountryResponse(
                country.id(), country.name(), country.code(),
                country.phoneCode(), country.isActive(), country.createdAt()
        );
    }
}
