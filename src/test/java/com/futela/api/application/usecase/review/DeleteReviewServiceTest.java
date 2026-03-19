package com.futela.api.application.usecase.review;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
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
class DeleteReviewServiceTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @InjectMocks
    private DeleteReviewService service;

    @Test
    @DisplayName("Doit effectuer une suppression logique avec succès")
    void shouldSoftDeleteSuccessfully() {
        UUID reviewId = UUID.randomUUID();
        ReviewEntity review = new ReviewEntity();

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId);

        assertThat(review.getDeletedAt()).isNotNull();
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("Doit rejeter quand l'avis n'existe pas")
    void shouldRejectWhenReviewNotFound() {
        UUID reviewId = UUID.randomUUID();

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(reviewId));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit définir deletedAt lors de la suppression logique")
    void shouldSetDeletedAtTimestamp() {
        UUID reviewId = UUID.randomUUID();
        ReviewEntity review = new ReviewEntity();

        when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reviewId);

        assertThat(review.isDeleted()).isTrue();
    }
}
