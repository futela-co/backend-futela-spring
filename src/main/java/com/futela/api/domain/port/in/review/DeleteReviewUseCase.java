package com.futela.api.domain.port.in.review;

import java.util.UUID;

public interface DeleteReviewUseCase {
    void execute(UUID reviewId);
}
