package com.futela.api.domain.port.in.review;

import com.futela.api.application.dto.response.review.ReviewResponse;

import java.util.UUID;

public interface GetReviewByIdUseCase {
    ReviewResponse execute(UUID reviewId);
}
