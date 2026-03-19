package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.ApproveReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ApproveReviewService implements ApproveReviewUseCase {

    private final JpaReviewRepository reviewRepository;

    public ApproveReviewService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse execute(UUID reviewId) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        if (entity.isApproved()) {
            throw new InvalidOperationException("Cet avis est déjà approuvé");
        }

        entity.setApproved(true);
        entity.setFlagged(false);
        entity.setFlagReason(null);

        ReviewEntity saved = reviewRepository.save(entity);
        return ReviewResponseMapper.fromEntity(saved);
    }
}
