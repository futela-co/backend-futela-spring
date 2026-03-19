package com.futela.api.infrastructure.persistence.repository.reservation;

import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaVisitRepository extends JpaRepository<VisitEntity, UUID> {

    @Query("SELECT v FROM VisitEntity v LEFT JOIN FETCH v.property LEFT JOIN FETCH v.user WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<VisitEntity> findByIdAndDeletedAtIsNull(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"property", "user"})
    List<VisitEntity> findByPropertyIdAndDeletedAtIsNull(UUID propertyId);

    @EntityGraph(attributePaths = {"property", "user"})
    List<VisitEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    @EntityGraph(attributePaths = {"property", "user"})
    Page<VisitEntity> findByUserIdAndStatusInAndDeletedAtIsNull(UUID userId, List<VisitStatus> statuses, Pageable pageable);
}
