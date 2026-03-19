package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.Province;
import com.futela.api.infrastructure.persistence.entity.address.ProvinceEntity;

public final class ProvincePersistenceMapper {

    private ProvincePersistenceMapper() {}

    public static Province toDomain(ProvinceEntity entity) {
        return new Province(
                entity.getId(), entity.getName(), entity.getCode(),
                entity.isActive(),
                entity.getCountry().getId(),
                entity.getCountry().getName(),
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    public static ProvinceEntity toEntity(Province domain, com.futela.api.infrastructure.persistence.entity.address.CountryEntity country) {
        var entity = new ProvinceEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setCode(domain.code());
        entity.setActive(domain.isActive());
        entity.setCountry(country);
        return entity;
    }
}
