package com.futela.api.infrastructure.persistence.repository.reservation;

import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaVisitRepository extends JpaRepository<VisitEntity, UUID> {

    Optional<VisitEntity> findByIdAndDeletedAtIsNull(UUID id);

    List<VisitEntity> findByPropertyIdAndDeletedAtIsNull(UUID propertyId);

    List<VisitEntity> findByUserIdAndDeletedAtIsNull(UUID userId);
}
