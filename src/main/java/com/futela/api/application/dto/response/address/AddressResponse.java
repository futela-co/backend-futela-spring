package com.futela.api.application.dto.response.address;

import com.futela.api.domain.model.address.Address;

import java.time.Instant;
import java.util.UUID;

public record AddressResponse(
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
        String formattedAddress,
        Instant createdAt
) {
    public static AddressResponse fromDomain(Address address) {
        // Build formatted address
        StringBuilder sb = new StringBuilder();
        if (address.number() != null && address.street() != null) {
            sb.append(address.number()).append(" ").append(address.street());
        } else if (address.street() != null) {
            sb.append(address.street());
        }
        if (address.districtName() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(address.districtName());
        }
        if (address.townName() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(address.townName());
        }
        if (address.cityName() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(address.cityName());
        }

        return new AddressResponse(
                address.id(), address.street(), address.number(),
                address.additionalInfo(), address.latitude(), address.longitude(),
                address.districtId(), address.districtName(),
                address.townId(), address.townName(),
                address.cityId(), address.cityName(),
                address.provinceId(), address.provinceName(),
                address.countryId(), address.countryName(),
                sb.toString(), address.createdAt()
        );
    }
}
