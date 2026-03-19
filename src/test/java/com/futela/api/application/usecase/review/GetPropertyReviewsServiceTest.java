package com.futela.api.application.usecase.review;

import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.review.ReviewEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.review.JpaReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPropertyReviewsServiceTest {

    @Mock
    private JpaReviewRepository reviewRepository;

    @InjectMocks
    private GetPropertyReviewsService service;

    private UUID propertyId;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Doit retourner uniquement les avis approuvés")
    void shouldReturnOnlyApprovedReviews() {
        ReviewEntity review1 = createReviewEntity(4, "Très bien", true);
        ReviewEntity review2 = createReviewEntity(5, "Excellent", true);

        when(reviewRepository.findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(propertyId))
                .thenReturn(List.of(review1, review2));

        List<ReviewResponse> result = service.execute(propertyId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Doit retourner une liste vide quand aucun avis approuvé")
    void shouldReturnEmptyListWhenNoApprovedReviews() {
        when(reviewRepository.findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(propertyId))
                .thenReturn(Collections.emptyList());

        List<ReviewResponse> result = service.execute(propertyId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit mapper correctement les entités en réponses")
    void shouldMapEntitiesToResponsesCorrectly() {
        ReviewEntity review = createReviewEntity(5, "Parfait", true);

        when(reviewRepository.findByPropertyIdAndIsApprovedTrueAndDeletedAtIsNull(propertyId))
                .thenReturn(List.of(review));

        List<ReviewResponse> result = service.execute(propertyId);

        assertThat(result).hasSize(1);
        ReviewResponse response = result.getFirst();
        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.comment()).isEqualTo("Parfait");
        assertThat(response.isApproved()).isTrue();
    }

    private ReviewEntity createReviewEntity(int rating, String comment, boolean approved) {
        UserEntity user = new UserEntity();
        user.setFirstName("User");
        user.setLastName("Test");

        PropertyEntity property = org.mockito.Mockito.mock(PropertyEntity.class);
        when(property.getId()).thenReturn(propertyId);
        when(property.getTitle()).thenReturn("Test Property");

        ReviewEntity review = new ReviewEntity();
        review.setProperty(property);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setApproved(approved);

        // Set required BaseEntity fields via reflection
        try {
            var idField = review.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(review, UUID.randomUUID());
            var createdAtField = review.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(review, Instant.now());
            var updatedAtField = review.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(review, Instant.now());
        } catch (Exception ignored) {}

        return review;
    }
}
