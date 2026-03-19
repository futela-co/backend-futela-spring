package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveReviewServiceTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @InjectMocks
    private ApproveReviewService service;

    private UUID reviewId;
    private ReviewEntity review;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setFirstName("Reviewer");
        user.setLastName("Test");

        PropertyEntity property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(UUID.randomUUID());
        lenient().when(property.getTitle()).thenReturn("Test Property");

        review = new ReviewEntity();
        review.setProperty(property);
        review.setUser(user);
        review.setRating(4);
        review.setComment("Très bien");
    }

    @Test
    @DisplayName("Doit approuver un avis et définir isApproved à true")
    void shouldApproveReviewAndSetIsApprovedTrue() {
        review.setApproved(false);
        review.setFlagged(true);
        review.setFlagReason("Suspect");

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId);

        assertThat(review.isApproved()).isTrue();
        assertThat(review.isFlagged()).isFalse();
        assertThat(review.getFlagReason()).isNull();
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("Doit rejeter quand l'avis n'existe pas")
    void shouldRejectWhenReviewNotFound() {
        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(reviewId));
    }

    @Test
    @DisplayName("Doit rejeter quand l'avis est déjà approuvé")
    void shouldRejectWhenAlreadyApproved() {
        review.setApproved(true);

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));

        assertThrows(InvalidOperationException.class, () -> service.execute(reviewId));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit réinitialiser le signalement lors de l'approbation")
    void shouldClearFlagWhenApproving() {
        review.setApproved(false);
        review.setFlagged(true);
        review.setFlagReason("Contenu inapproprié");

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId);

        assertThat(review.isFlagged()).isFalse();
        assertThat(review.getFlagReason()).isNull();
    }
}
