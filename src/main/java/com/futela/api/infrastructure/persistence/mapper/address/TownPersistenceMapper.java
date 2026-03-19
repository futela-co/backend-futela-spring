package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.Town;
import com.futela.api.infrastructure.persistence.entity.address.CityEntity;
import com.futela.api.infrastructure.persistence.entity.address.TownEntity;

public final class TownPersistenceMapper {

    private TownPersistenceMapper() {}

    public static Town toDomain(TownEntity entity) {
        return new Town(
                entity.getId(), entity.getName(), entity.getZipCode(),
                entity.isActive(),
                entity.getCity().getId(),
                entity.getCity().getName(),
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    public static TownEntity toEntity(Town domain, CityEntity city) {
        var entity = new TownEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setZipCode(domain.zipCode());
        entity.setActive(domain.isActive());
        entity.setCity(city);
        return entity;
    }
}
