package com.futela.api.domain.port.in.review;

import com.futela.api.application.dto.request.review.UpdateReviewRequest;
import com.futela.api.application.dto.response.review.ReviewResponse;

import java.util.UUID;

public interface UpdateReviewUseCase {
    ReviewResponse execute(UUID reviewId, UpdateReviewRequest request, UUID currentUserId);
}
