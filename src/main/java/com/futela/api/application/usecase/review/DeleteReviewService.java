package com.futela.api.application.usecase.review;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.DeleteReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteReviewService implements DeleteReviewUseCase {

    private final JpaReviewRepository reviewRepository;

    public DeleteReviewService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(UUID reviewId) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        entity.softDelete();
        reviewRepository.save(entity);
    }
}
