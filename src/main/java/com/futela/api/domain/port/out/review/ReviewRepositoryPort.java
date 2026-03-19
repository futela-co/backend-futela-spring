package com.futela.api.domain.port.out.review;

import com.futela.api.domain.model.review.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepositoryPort {

    Review save(Review review);

    Optional<Review> findById(UUID id);

    List<Review> findApprovedByPropertyId(UUID propertyId);

    List<Review> findByUserId(UUID userId);

    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);

    void softDelete(UUID id);
}
