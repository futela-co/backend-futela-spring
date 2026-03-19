package com.futela.api.domain.port.in.review;

import com.futela.api.application.dto.request.review.CreateReviewRequest;
import com.futela.api.application.dto.response.review.ReviewResponse;

import java.util.UUID;

public interface CreateReviewUseCase {
    ReviewResponse execute(CreateReviewRequest request, UUID currentUserId);
}
