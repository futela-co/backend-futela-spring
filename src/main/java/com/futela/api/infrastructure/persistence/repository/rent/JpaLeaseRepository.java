package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.infrastructure.persistence.entity.rent.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLeaseRepository extends JpaRepository<LeaseEntity, UUID> {
    List<LeaseEntity> findByLandlordIdAndDeletedAtIsNull(UUID landlordId);
    List<LeaseEntity> findByTenantIdAndDeletedAtIsNull(UUID tenantId);
    Optional<LeaseEntity> findByPropertyIdAndStatusAndDeletedAtIsNull(UUID propertyId, LeaseStatus status);
    List<LeaseEntity> findByStatusAndDeletedAtIsNull(LeaseStatus status);
    List<LeaseEntity> findByDeletedAtIsNull();
    long countByLandlordIdAndDeletedAtIsNull(UUID landlordId);
    long countByLandlordIdAndStatusAndDeletedAtIsNull(UUID landlordId, LeaseStatus status);

    @Query("SELECT l FROM LeaseEntity l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<LeaseEntity> findByIdAndNotDeleted(UUID id);
}
