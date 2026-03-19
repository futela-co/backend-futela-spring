package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.port.in.review.GetPropertyReviewsUseCase;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetPropertyReviewsService implements GetPropertyReviewsUseCase {

    private final JpaReviewRepository reviewRepository;

    public GetPropertyReviewsService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<ReviewResponse> execute(UUID propertyId) {
        return reviewRepository.findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(propertyId).stream()
                .map(ReviewResponseMapper::fromEntity)
                .toList();
    }
}
