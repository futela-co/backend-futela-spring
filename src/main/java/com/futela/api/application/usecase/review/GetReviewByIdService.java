package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.GetReviewByIdUseCase;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetReviewByIdService implements GetReviewByIdUseCase {

    private final JpaReviewRepository reviewRepository;

    public GetReviewByIdService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse execute(UUID reviewId) {
        return reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .map(ReviewResponseMapper::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));
    }
}
