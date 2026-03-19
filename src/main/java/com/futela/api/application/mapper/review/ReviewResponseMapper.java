package com.futela.api.application.mapper.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;

public final class ReviewResponseMapper {

    private ReviewResponseMapper() {}

    public static ReviewResponse fromEntity(ReviewEntity entity) {
        return new ReviewResponse(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getProperty().getTitle(),
                entity.getUser().getId(),
                entity.getUser().getFirstName() + " " + entity.getUser().getLastName(),
                entity.getRating(),
                entity.getComment(),
                entity.isApproved(),
                entity.isFlagged(),
                entity.getFlagReason(),
                entity.getOwnerResponse(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
