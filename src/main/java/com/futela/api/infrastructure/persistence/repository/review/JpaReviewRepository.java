package com.futela.api.infrastructure.persistence.repository.review;

import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    @Query("SELECT r FROM ReviewEntity r LEFT JOIN FETCH r.property LEFT JOIN FETCH r.user LEFT JOIN FETCH r.reviewee LEFT JOIN FETCH r.reservation LEFT JOIN FETCH r.moderatedBy WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<ReviewEntity> findByIdAndDeletedAtIsNull(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"property", "user", "reviewee"})
    List<ReviewEntity> findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(UUID propertyId);

    @EntityGraph(attributePaths = {"property", "user", "reviewee"})
    List<ReviewEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndPropertyIdAndDeletedAtIsNull(UUID userId, UUID propertyId);

    @EntityGraph(attributePaths = {"property", "user", "reviewee"})
    Page<ReviewEntity> findByIsApprovedTrueAndDeletedAtIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"property", "user", "reviewee"})
    Page<ReviewEntity> findByIsApprovedFalseAndDeletedAtIsNull(Pageable pageable);

    long countByDeletedAtIsNull();

    long countByIsApprovedFalseAndDeletedAtIsNull();
}
