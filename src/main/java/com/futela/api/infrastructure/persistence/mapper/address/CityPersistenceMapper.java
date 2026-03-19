package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.City;
import com.futela.api.infrastructure.persistence.entity.address.CityEntity;
import com.futela.api.infrastructure.persistence.entity.address.ProvinceEntity;

public final class CityPersistenceMapper {

    private CityPersistenceMapper() {}

    public static City toDomain(CityEntity entity) {
        return new City(
                entity.getId(), entity.getName(), entity.getZipCode(),
                entity.isActive(),
                entity.getProvince().getId(),
                entity.getProvince().getName(),
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    public static CityEntity toEntity(City domain, ProvinceEntity province) {
        var entity = new CityEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setZipCode(domain.zipCode());
        entity.setActive(domain.isActive());
        entity.setProvince(province);
        return entity;
    }
}
