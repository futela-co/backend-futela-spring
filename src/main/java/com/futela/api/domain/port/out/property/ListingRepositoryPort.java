package com.futela.api.domain.port.out.property;

import com.futela.api.domain.model.property.Listing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepositoryPort {
    Listing save(Listing listing);
    void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId);
    List<Listing> findByUserId(UUID userId);
    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);
    Optional<Listing> findByUserIdAndPropertyId(UUID userId, UUID propertyId);
}
