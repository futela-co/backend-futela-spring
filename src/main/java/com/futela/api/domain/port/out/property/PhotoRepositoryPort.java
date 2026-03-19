package com.futela.api.domain.port.out.property;

import com.futela.api.domain.model.property.Photo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepositoryPort {
    Photo save(Photo photo);
    Optional<Photo> findById(UUID id);
    List<Photo> findByPropertyId(UUID propertyId);
    void deleteById(UUID id);
    int countByPropertyId(UUID propertyId);
    void unsetPrimaryByPropertyId(UUID propertyId);
    void saveAll(List<Photo> photos);
}
