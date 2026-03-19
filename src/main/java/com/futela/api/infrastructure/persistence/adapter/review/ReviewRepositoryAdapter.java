package com.futela.api.infrastructure.persistence.adapter.review;

import com.futela.api.domain.model.review.Review;
import com.futela.api.domain.port.out.review.ReviewRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.mapper.review.ReviewPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReviewRepositoryAdapter implements ReviewRepositoryPort {

    private final JpaReviewRepository jpaRepository;

    public ReviewRepositoryAdapter(JpaReviewRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Review save(Review review) {
        ReviewEntity entity;

        if (review.id() != null) {
            entity = jpaRepository.findById(review.id()).orElse(new ReviewEntity());
        } else {
            entity = new ReviewEntity();
        }

        ReviewPersistenceMapper.updateEntity(entity, review);
        ReviewEntity saved = jpaRepository.save(entity);
        return ReviewPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public List<Review> findApprovedByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(propertyId).stream()
                .map(ReviewPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Review> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(ReviewPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        return jpaRepository.existsByUserIdAndPropertyIdAndDeletedAtIsNull(userId, propertyId);
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findByIdAndDeletedAtIsNull(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
