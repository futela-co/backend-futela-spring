package com.futela.api.infrastructure.persistence.mapper.address;

import com.futela.api.domain.model.address.District;
import com.futela.api.infrastructure.persistence.entity.address.DistrictEntity;

public final class DistrictPersistenceMapper {

    private DistrictPersistenceMapper() {}

    public static District toDomain(DistrictEntity entity) {
        return new District(
                entity.getId(), entity.getName(), entity.isActive(),
                entity.getCity() != null ? entity.getCity().getId() : null,
                entity.getCity() != null ? entity.getCity().getName() : null,
                entity.getTown() != null ? entity.getTown().getId() : null,
                entity.getTown() != null ? entity.getTown().getName() : null,
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}
