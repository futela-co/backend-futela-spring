package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.request.review.CreateReviewRequest;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateReviewServiceTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateReviewService service;

    private UUID userId;
    private UUID propertyId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Doit rejeter quand la propriété n'existe pas")
    void shouldRejectWhenPropertyNotFound() {
        CreateReviewRequest request = new CreateReviewRequest(propertyId, 4, "Great");
        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(null);

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit rejeter quand le propriétaire tente de laisser un avis")
    void shouldRejectOwnerSelfReview() {
        CreateReviewRequest request = new CreateReviewRequest(propertyId, 4, "Great");

        // Mock owner with getId() returning ownerId
        UserEntity ownerMock = mock(UserEntity.class);
        when(ownerMock.getId()).thenReturn(ownerId);

        // Mock property with getOwner() returning ownerMock
        PropertyEntity mockProperty = mock(PropertyEntity.class);
        when(mockProperty.getOwner()).thenReturn(ownerMock);

        // Mock user (same as owner)
        UserEntity userMock = mock(UserEntity.class);

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(mockProperty);
        lenient().when(entityManager.find(UserEntity.class, ownerId)).thenReturn(userMock);

        assertThatThrownBy(() -> service.execute(request, ownerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("propriétaire");
    }

    @Test
    @DisplayName("Doit rejeter quand un avis existe déjà pour cette propriété")
    void shouldRejectDuplicateReview() {
        CreateReviewRequest request = new CreateReviewRequest(propertyId, 4, "Great");

        // Mock owner with different ID than userId
        UserEntity ownerMock = mock(UserEntity.class);
        when(ownerMock.getId()).thenReturn(ownerId);

        PropertyEntity mockProperty = mock(PropertyEntity.class);
        when(mockProperty.getOwner()).thenReturn(ownerMock);

        UserEntity userMock = mock(UserEntity.class);

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(mockProperty);
        lenient().when(entityManager.find(UserEntity.class, userId)).thenReturn(userMock);
        when(reviewRepository.existsByUserIdAndPropertyIdAndDeletedAtIsNull(userId, propertyId)).thenReturn(true);

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("déjà");
    }

    @Test
    @DisplayName("Doit rejeter quand la note est invalide")
    void shouldRejectInvalidRating() {
        CreateReviewRequest request = new CreateReviewRequest(propertyId, 6, "Invalid");

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(ValidationException.class);
    }
}
