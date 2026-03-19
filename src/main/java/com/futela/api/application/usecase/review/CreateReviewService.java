package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.request.review.CreateReviewRequest;
import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.review.ReviewResponseMapper;
import com.futela.api.domain.event.ReviewCreatedEvent;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.port.in.review.CreateReviewUseCase;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CreateReviewService implements CreateReviewUseCase {

    private final JpaReviewRepository reviewRepository;
    private final jakarta.persistence.EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    public CreateReviewService(
            JpaReviewRepository reviewRepository,
            jakarta.persistence.EntityManager entityManager,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reviewRepository = reviewRepository;
        this.entityManager = entityManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReviewResponse execute(CreateReviewRequest request, UUID currentUserId) {
        // Validate rating
        if (request.rating() < 1 || request.rating() > 5) {
            throw new ValidationException("La note doit être comprise entre 1 et 5");
        }

        // Load property
        PropertyEntity property = entityManager.find(PropertyEntity.class, request.propertyId());
        if (property == null) {
            throw new ResourceNotFoundException("Propriété", request.propertyId().toString());
        }

        // Load user
        UserEntity user = entityManager.find(UserEntity.class, currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException("Utilisateur", currentUserId.toString());
        }

        // Owner cannot review own property
        if (property.getOwner().getId().equals(currentUserId)) {
            throw new InvalidOperationException("Le propriétaire ne peut pas laisser un avis sur sa propre propriété");
        }

        // One review per user per property
        if (reviewRepository.existsByUserIdAndPropertyIdAndDeletedAtIsNull(currentUserId, request.propertyId())) {
            throw new DuplicateResourceException("Vous avez déjà laissé un avis pour cette propriété");
        }

        // Create review entity
        ReviewEntity entity = new ReviewEntity();
        entity.setProperty(property);
        entity.setUser(user);
        entity.setCompany(property.getCompany());
        entity.setRating(request.rating());
        entity.setComment(request.comment());
        entity.setApproved(false);
        entity.setFlagged(false);

        ReviewEntity saved = reviewRepository.save(entity);

        // Publish event
        eventPublisher.publishEvent(new ReviewCreatedEvent(
                saved.getId(), property.getId(), user.getId(), request.rating()
        ));

        return ReviewResponseMapper.fromEntity(saved);
    }
}
