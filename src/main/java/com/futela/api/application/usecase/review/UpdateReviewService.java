package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.request.review.UpdateReviewRequest;
import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.UpdateReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateReviewService implements UpdateReviewUseCase {

    private final JpaReviewRepository reviewRepository;

    public UpdateReviewService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse execute(UUID reviewId, UpdateReviewRequest request, UUID currentUserId) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        if (!entity.getUser().getId().equals(currentUserId)) {
            throw new InvalidOperationException("Vous ne pouvez modifier que vos propres avis");
        }

        if (request.rating() != null) {
            entity.setRating(request.rating());
        }

        if (request.comment() != null) {
            entity.setComment(request.comment());
        }

        // Reset approval status for re-moderation
        entity.setApproved(false);

        ReviewEntity saved = reviewRepository.save(entity);
        return ReviewResponseMapper.fromEntity(saved);
    }
}
