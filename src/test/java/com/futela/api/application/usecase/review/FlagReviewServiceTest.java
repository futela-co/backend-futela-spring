package com.futela.api.application.usecase.review;

import com.futela.api.domain.event.ReviewFlaggedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlagReviewServiceTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FlagReviewService service;

    private UUID reviewId;
    private UUID propertyId;
    private ReviewEntity review;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setFirstName("Reviewer");
        user.setLastName("Test");

        PropertyEntity property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(propertyId);
        lenient().when(property.getTitle()).thenReturn("Test Property");

        review = new ReviewEntity();
        review.setProperty(property);
        review.setUser(user);
        review.setRating(3);
        review.setComment("Moyen");
    }

    @Test
    @DisplayName("Doit signaler un avis et définir isFlagged à true")
    void shouldFlagReviewAndSetIsFlaggedTrue() {
        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId, "Contenu inapproprié");

        assertThat(review.isFlagged()).isTrue();
        assertThat(review.getFlagReason()).isEqualTo("Contenu inapproprié");
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("Doit publier un événement ReviewFlaggedEvent après le signalement")
    void shouldEmitReviewFlaggedEvent() {
        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId, "Spam");

        ArgumentCaptor<ReviewFlaggedEvent> eventCaptor = ArgumentCaptor.forClass(ReviewFlaggedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ReviewFlaggedEvent event = eventCaptor.getValue();
        assertThat(event.propertyId()).isEqualTo(propertyId);
        assertThat(event.reason()).isEqualTo("Spam");
    }

    @Test
    @DisplayName("Doit rejeter quand l'avis n'existe pas")
    void shouldRejectWhenReviewNotFound() {
        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(reviewId, "Raison"));
    }
}
