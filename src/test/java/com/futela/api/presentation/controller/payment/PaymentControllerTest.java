package com.futela.api.presentation.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.domain.port.in.payment.*;
import com.futela.api.infrastructure.security.CorsProperties;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest({PaymentController.class, CurrencyController.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // PaymentController dependencies
    @MockitoBean
    private InitiatePaymentUseCase initiatePaymentUseCase;

    @MockitoBean
    private GetTransactionByIdUseCase getTransactionByIdUseCase;

    @MockitoBean
    private GetUserTransactionsUseCase getUserTransactionsUseCase;

    @MockitoBean
    private GetPendingTransactionsUseCase getPendingTransactionsUseCase;

    @MockitoBean
    private CancelPaymentUseCase cancelPaymentUseCase;

    @MockitoBean
    private RefundPaymentUseCase refundPaymentUseCase;

    // CurrencyController dependencies
    @MockitoBean
    private GetCurrenciesUseCase getCurrenciesUseCase;

    @MockitoBean
    private GetActiveCurrenciesUseCase getActiveCurrenciesUseCase;

    @MockitoBean
    private CreateCurrencyUseCase createCurrencyUseCase;

    @MockitoBean
    private UpdateCurrencyUseCase updateCurrencyUseCase;

    @MockitoBean
    private ConvertCurrencyUseCase convertCurrencyUseCase;

    // Security dependencies
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private CorsProperties corsProperties;

    @MockitoBean
    private EntityManager entityManager;

    @Test
    @DisplayName("POST /api/payments/initiate doit exiger l'authentification")
    void initiatePaymentShouldRequireAuth() throws Exception {
        when(corsProperties.allowedOrigins()).thenReturn(List.of("*"));

        InitiatePaymentRequest request = new InitiatePaymentRequest(
                new BigDecimal("100.00"), "USD", "0812345678",
                UUID.randomUUID(), "Paiement loyer", UUID.randomUUID()
        );

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assert statusCode == 401 || statusCode == 403
                            : "Attendu 401 ou 403 mais reçu " + statusCode;
                });
    }

    @Test
    @DisplayName("GET /api/currencies doit exiger l'authentification (endpoint protégé)")
    void getCurrenciesShouldRequireAuth() throws Exception {
        when(corsProperties.allowedOrigins()).thenReturn(List.of("*"));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assert statusCode == 401 || statusCode == 403
                            : "Attendu 401 ou 403 mais reçu " + statusCode;
                });
    }
}
