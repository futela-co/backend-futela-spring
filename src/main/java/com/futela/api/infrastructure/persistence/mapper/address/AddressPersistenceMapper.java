package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.Address;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;

public final class AddressPersistenceMapper {

    private AddressPersistenceMapper() {}

    public static Address toDomain(AddressEntity entity) {
        return new Address(
                entity.getId(),
                entity.getStreet(), entity.getNumber(), entity.getAdditionalInfo(),
                entity.getLatitude(), entity.getLongitude(),
                entity.getDistrict() != null ? entity.getDistrict().getId() : null,
                entity.getDistrict() != null ? entity.getDistrict().getName() : null,
                entity.getTown().getId(), entity.getTown().getName(),
                entity.getCity().getId(), entity.getCity().getName(),
                entity.getProvince().getId(), entity.getProvince().getName(),
                entity.getCountry().getId(), entity.getCountry().getName(),
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}
