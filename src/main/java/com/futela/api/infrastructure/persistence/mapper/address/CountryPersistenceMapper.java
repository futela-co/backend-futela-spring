package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.Country;
import com.futela.api.infrastructure.persistence.entity.address.CountryEntity;

public final class CountryPersistenceMapper {

    private CountryPersistenceMapper() {}

    public static Country toDomain(CountryEntity entity) {
        return new Country(
                entity.getId(), entity.getName(), entity.getCode(),
                entity.getPhoneCode(), entity.isActive(),
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    public static CountryEntity toEntity(Country domain) {
        var entity = new CountryEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setCode(domain.code().toUpperCase());
        entity.setPhoneCode(domain.phoneCode());
        entity.setActive(domain.isActive());
        return entity;
    }
}
