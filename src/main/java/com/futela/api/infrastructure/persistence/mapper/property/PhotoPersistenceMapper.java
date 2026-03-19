package com.futela.api.infrastructure.persistence.mapper.property;

import com.futela.api.domain.model.property.Photo;
import com.futela.api.infrastructure.persistence.entity.property.PhotoEntity;

public final class PhotoPersistenceMapper {

    private PhotoPersistenceMapper() {}

    public static Photo toDomain(PhotoEntity entity) {
        return new Photo(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getUrl(),
                entity.getCaption(),
                entity.getDisplayOrder(),
                entity.isPrimary(),
                entity.getCreatedAt()
        );
    }
}
