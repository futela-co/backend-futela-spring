package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.port.in.review.GetPendingReviewsUseCase;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetPendingReviewsService implements GetPendingReviewsUseCase {

    private final JpaReviewRepository reviewRepository;

    public GetPendingReviewsService(JpaReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Page<ReviewResponse> execute(Pageable pageable) {
        return reviewRepository.findByIsApprovedFalseAndDeletedAtIsNull(pageable)
                .map(ReviewResponseMapper::fromEntity);
    }
}
