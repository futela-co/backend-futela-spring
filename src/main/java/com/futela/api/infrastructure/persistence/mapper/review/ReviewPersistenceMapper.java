package com.futela.api.infrastructure.persistence.mapper.review;

import com.futela.api.domain.model.review.Review;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;

public final class ReviewPersistenceMapper {

    private ReviewPersistenceMapper() {}

    public static Review toDomain(ReviewEntity entity) {
        return new Review(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getUser().getId(),
                entity.getCompany().getId(),
                entity.getRating(),
                entity.getComment(),
                entity.isApproved(),
                entity.isFlagged(),
                entity.getFlagReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static void updateEntity(ReviewEntity entity, Review domain) {
        entity.setRating(domain.rating());
        entity.setComment(domain.comment());
        entity.setApproved(domain.isApproved());
        entity.setFlagged(domain.isFlagged());
        entity.setFlagReason(domain.flagReason());
    }
}
