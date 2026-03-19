package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.event.ReviewFlaggedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.review.FlagReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FlagReviewService implements FlagReviewUseCase {

    private final JpaReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FlagReviewService(
            JpaReviewRepository reviewRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reviewRepository = reviewRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReviewResponse execute(UUID reviewId, String reason) {
        ReviewEntity entity = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", reviewId.toString()));

        entity.setFlagged(true);
        entity.setFlagReason(reason);

        ReviewEntity saved = reviewRepository.save(entity);

        eventPublisher.publishEvent(new ReviewFlaggedEvent(
                saved.getId(), saved.getProperty().getId(), reason
        ));

        return ReviewResponseMapper.fromEntity(saved);
    }
}
