package com.futela.api.infrastructure.persistence.repository.review;

import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    Optional<ReviewEntity> findByIdAndDeletedAtIsNull(UUID id);

    List<ReviewEntity> findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(UUID propertyId);

    List<ReviewEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndPropertyIdAndDeletedAtIsNull(UUID userId, UUID propertyId);
}
