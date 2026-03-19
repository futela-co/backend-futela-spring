package com.futela.api.infrastructure.persistence.repository.property;

import com.futela.api.infrastructure.persistence.entity.property.ListingEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaListingRepository extends JpaRepository<ListingEntity, UUID> {

    @EntityGraph(attributePaths = {"property", "user"})
    List<ListingEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);
    Optional<ListingEntity> findByUserIdAndPropertyId(UUID userId, UUID propertyId);
    void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId);
}
