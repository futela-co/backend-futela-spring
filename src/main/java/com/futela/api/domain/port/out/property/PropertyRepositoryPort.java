package com.futela.api.domain.port.out.property;

import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.model.property.Property;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepositoryPort {
    Property save(Property property);
    Optional<Property> findById(UUID id);
    Optional<Property> findBySlug(String slug);
    PageResult<Property> findByOwnerId(UUID ownerId, int page, int size);
    PageResult<Property> search(PropertySearchCriteria criteria);
    void softDelete(UUID id);
}
