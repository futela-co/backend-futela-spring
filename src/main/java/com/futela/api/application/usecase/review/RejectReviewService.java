package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.RejectReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RejectReviewService implements RejectReviewUseCase {

    private final JpaReviewRepository reviewRepository;

    public RejectReviewService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse execute(UUID reviewId) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        entity.setApproved(false);

        ReviewEntity saved = reviewRepository.save(entity);
        return ReviewResponseMapper.fromEntity(saved);
    }
}
