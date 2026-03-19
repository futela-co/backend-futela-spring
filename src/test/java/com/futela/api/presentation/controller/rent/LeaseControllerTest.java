package com.futela.api.presentation.controller.rent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futela.api.application.dto.request.rent.CreateLeaseRequest;
import com.futela.api.domain.port.in.rent.*;
import com.futela.api.infrastructure.security.CorsProperties;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import com.futela.api.presentation.filter.TenantContextFilter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaseController.class)
class LeaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateLeaseUseCase createLeaseUseCase;

    @MockitoBean
    private GetLeaseByIdUseCase getLeaseByIdUseCase;

    @MockitoBean
    private GetLandlordLeasesUseCase getLandlordLeasesUseCase;

    @MockitoBean
    private GetTenantLeasesUseCase getTenantLeasesUseCase;

    @MockitoBean
    private GetActiveLeasesUseCase getActiveLeasesUseCase;

    @MockitoBean
    private RenewLeaseUseCase renewLeaseUseCase;

    @MockitoBean
    private TerminateLeaseUseCase terminateLeaseUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private CorsProperties corsProperties;

    @MockitoBean
    private EntityManager entityManager;

    @Test
    @DisplayName("POST /api/leases doit exiger l'authentification")
    void createLeaseShouldRequireAuth() throws Exception {
        when(corsProperties.allowedOrigins()).thenReturn(List.of("*"));

        CreateLeaseRequest request = new CreateLeaseRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31),
                5, null
        );

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assert statusCode == 401 || statusCode == 403
                            : "Attendu 401 ou 403 mais reçu " + statusCode;
                });
    }

    @Test
    @DisplayName("GET /api/leases/landlord doit exiger l'authentification")
    void getLandlordLeasesShouldRequireAuth() throws Exception {
        when(corsProperties.allowedOrigins()).thenReturn(List.of("*"));

        mockMvc.perform(get("/api/leases/landlord")
                        .param("landlordId", UUID.randomUUID().toString()))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assert statusCode == 401 || statusCode == 403
                            : "Attendu 401 ou 403 mais reçu " + statusCode;
                });
    }
}
