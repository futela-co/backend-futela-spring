package com.futela.api.presentation.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.review.*;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateReviewUseCase createReviewUseCase;

    @MockitoBean
    private GetReviewByIdUseCase getReviewByIdUseCase;

    @MockitoBean
    private GetPropertyReviewsUseCase getPropertyReviewsUseCase;

    @MockitoBean
    private DeleteReviewUseCase deleteReviewUseCase;

    @MockitoBean
    private ApproveReviewUseCase approveReviewUseCase;

    @MockitoBean
    private RejectReviewUseCase rejectReviewUseCase;

    @MockitoBean
    private FlagReviewUseCase flagReviewUseCase;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EntityManager entityManager;

    private UUID propertyId;
    private UUID reviewId;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        reviewResponse = new ReviewResponse(
                reviewId,
                propertyId,
                "Appartement Gombe",
                UUID.randomUUID(),
                "Jean Dupont",
                5,
                "Excellent séjour",
                true,
                false,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("GET /api/properties/{id}/reviews doit retourner 200 avec les avis")
    void shouldReturnPropertyReviews() throws Exception {
        when(getPropertyReviewsUseCase.execute(any(UUID.class))).thenReturn(List.of(reviewResponse));

        mockMvc.perform(get("/api/properties/{propertyId}/reviews", propertyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[0].comment").value("Excellent séjour"));
    }

    @Test
    @DisplayName("POST /api/reviews sans authentification doit retourner 401")
    void shouldReturn401WhenCreatingReviewWithoutAuth() throws Exception {
        String body = """
                {
                    "propertyId": "%s",
                    "rating": 4,
                    "comment": "Très bien"
                }
                """.formatted(propertyId);

        mockMvc.perform(post("/api/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@futela.com", roles = {"ADMIN"})
    @DisplayName("POST /api/reviews/{id}/approve doit retourner 200 pour un admin")
    void shouldApproveReviewAsAdmin() throws Exception {
        when(approveReviewUseCase.execute(any(UUID.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/reviews/{id}/approve", reviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("POST /api/reviews avec authentification doit retourner 201")
    void shouldCreateReviewWhenAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(createReviewUseCase.execute(any(), any(UUID.class))).thenReturn(reviewResponse);

        String body = """
                {
                    "propertyId": "%s",
                    "rating": 5,
                    "comment": "Excellent séjour"
                }
                """.formatted(propertyId);

        mockMvc.perform(post("/api/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("POST /api/reviews/{id}/flag doit retourner 200")
    void shouldFlagReviewSuccessfully() throws Exception {
        when(flagReviewUseCase.execute(any(UUID.class), any(String.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/reviews/{id}/flag", reviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Contenu inapproprié\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@futela.com", roles = {"ADMIN"})
    @DisplayName("POST /api/reviews/{id}/reject doit retourner 200 pour un admin")
    void shouldRejectReviewAsAdmin() throws Exception {
        when(rejectReviewUseCase.execute(any(UUID.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/reviews/{id}/reject", reviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
