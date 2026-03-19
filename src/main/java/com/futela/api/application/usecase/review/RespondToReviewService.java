package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.RespondToReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RespondToReviewService implements RespondToReviewUseCase {

    private final JpaReviewRepository reviewRepository;

    public RespondToReviewService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse execute(UUID reviewId, String response, UUID currentUserId) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        if (!entity.getReviewee().getId().equals(currentUserId)) {
            throw new InvalidOperationException("Seul le propriétaire concerné peut répondre à cet avis");
        }

        entity.setOwnerResponse(response);

        ReviewEntity saved = reviewRepository.save(entity);
        return ReviewResponseMapper.fromEntity(saved);
    }
}
