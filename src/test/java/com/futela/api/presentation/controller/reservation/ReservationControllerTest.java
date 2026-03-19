package com.futela.api.presentation.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.port.in.reservation.*;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateReservationUseCase createReservationUseCase;

    @MockitoBean
    private GetReservationByIdUseCase getReservationByIdUseCase;

    @MockitoBean
    private ConfirmReservationUseCase confirmReservationUseCase;

    @MockitoBean
    private CancelReservationUseCase cancelReservationUseCase;

    @MockitoBean
    private CompleteReservationUseCase completeReservationUseCase;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EntityManager entityManager;

    private UUID reservationId;
    private UUID userId;
    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        reservationResponse = new ReservationResponse(
                reservationId,
                UUID.randomUUID(),
                "Appartement Gombe",
                userId,
                "Jean Dupont",
                UUID.randomUUID(),
                ReservationStatus.CONFIRMED,
                "Confirmée",
                "green",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                BigDecimal.valueOf(400),
                "USD",
                2,
                null,
                null,
                Instant.now(),
                null,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("POST /api/reservations sans authentification doit retourner 401")
    void shouldReturn401WhenCreatingReservationWithoutAuth() throws Exception {
        String body = """
                {
                    "propertyId": "%s",
                    "startDate": "2026-04-01",
                    "endDate": "2026-04-05",
                    "guestCount": 2
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("POST /api/reservations/{id}/confirm doit retourner 200")
    void shouldConfirmReservationSuccessfully() throws Exception {
        when(confirmReservationUseCase.execute(any(UUID.class))).thenReturn(reservationResponse);

        mockMvc.perform(post("/api/reservations/{id}/confirm", reservationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("POST /api/reservations/{id}/cancel doit retourner 200")
    void shouldCancelReservationSuccessfully() throws Exception {
        ReservationResponse cancelledResponse = new ReservationResponse(
                reservationId, UUID.randomUUID(), "Appartement Gombe",
                userId, "Jean Dupont", UUID.randomUUID(),
                ReservationStatus.CANCELLED, "Annulée", "red",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(5),
                BigDecimal.valueOf(400), "USD", 2, null, "Changement de plans",
                null, Instant.now(), null,
                Instant.now(), Instant.now()
        );

        when(cancelReservationUseCase.execute(any(UUID.class), any())).thenReturn(cancelledResponse);

        mockMvc.perform(post("/api/reservations/{id}/cancel", reservationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Changement de plans\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("POST /api/reservations/{id}/complete doit retourner 200")
    void shouldCompleteReservationSuccessfully() throws Exception {
        ReservationResponse completedResponse = new ReservationResponse(
                reservationId, UUID.randomUUID(), "Appartement Gombe",
                userId, "Jean Dupont", UUID.randomUUID(),
                ReservationStatus.COMPLETED, "Terminée", "blue",
                LocalDate.now().minusDays(5), LocalDate.now(),
                BigDecimal.valueOf(400), "USD", 2, null, null,
                Instant.now(), null, Instant.now(),
                Instant.now(), Instant.now()
        );

        when(completeReservationUseCase.execute(any(UUID.class))).thenReturn(completedResponse);

        mockMvc.perform(post("/api/reservations/{id}/complete", reservationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
