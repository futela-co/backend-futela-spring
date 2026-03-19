package com.futela.api.infrastructure.persistence.mapper.property;

import com.futela.api.domain.model.property.Listing;
import com.futela.api.infrastructure.persistence.entity.property.ListingEntity;

public final class ListingPersistenceMapper {

    private ListingPersistenceMapper() {}

    public static Listing toDomain(ListingEntity entity) {
        return new Listing(
                entity.getId(),
                entity.getUser().getId(),
                entity.getProperty().getId(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCreatedAt()
        );
    }
}
