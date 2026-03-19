package com.futela.api.domain.port.in.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetApprovedReviewsUseCase {
    Page<ReviewResponse> execute(Pageable pageable);
}
