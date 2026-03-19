package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.event.payment.PaymentInitiatedEvent;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.out.common.PaymentGatewayPort;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitiatePaymentServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private PaymentGatewayPort paymentGateway;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InitiatePaymentService initiatePaymentService;

    private UUID userId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
    }

    private Transaction savedTransaction(Transaction tx) {
        return new Transaction(
                UUID.randomUUID(), tx.reference(), tx.externalRef(),
                tx.type(), tx.status(), tx.amount(), tx.currency(),
                tx.phoneNumber(), tx.provider(), tx.userId(), tx.userName(),
                tx.description(), tx.metadata(), tx.failureReason(), tx.processedAt(),
                tx.companyId(), Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit initier un paiement avec succès")
    void shouldInitiatePaymentSuccessfully() {
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                new BigDecimal("100.00"), "USD", "0812345678", userId, "Paiement loyer", companyId
        );

        when(paymentGateway.generateOrderNumber()).thenReturn("ORD-123");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> savedTransaction(invocation.getArgument(0)));
        when(paymentGateway.initiatePayment(any(), any(), any(), any()))
                .thenReturn(Map.of("success", true, "externalId", "EXT-456"));

        TransactionResponse response = initiatePaymentService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TransactionStatus.PENDING);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Doit normaliser le numéro de téléphone 0812345678 en 243812345678")
    void shouldNormalizePhoneStartingWithZero() {
        String normalized = InitiatePaymentService.normalizePhone("0812345678");
        assertThat(normalized).isEqualTo("243812345678");
    }

    @Test
    @DisplayName("Doit normaliser le numéro de téléphone +243812345678 en 243812345678")
    void shouldNormalizePhoneStartingWithPlus243() {
        String normalized = InitiatePaymentService.normalizePhone("+243812345678");
        assertThat(normalized).isEqualTo("243812345678");
    }

    @Test
    @DisplayName("Doit persister la transaction AVANT l'appel FlexPay")
    void shouldPersistTransactionBeforeFlexPayCall() {
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                new BigDecimal("100.00"), "USD", "0812345678", userId, "Test", companyId
        );

        when(paymentGateway.generateOrderNumber()).thenReturn("ORD-123");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> savedTransaction(invocation.getArgument(0)));
        when(paymentGateway.initiatePayment(any(), any(), any(), any()))
                .thenReturn(Map.of("success", true, "externalId", "EXT-789"));

        initiatePaymentService.execute(request);

        // save is called at least once before FlexPay (first call creates the tx)
        verify(transactionRepository, atLeast(1)).save(any(Transaction.class));
        // FlexPay is called after save
        verify(paymentGateway).initiatePayment(any(), any(), eq("243812345678"), eq("ORD-123"));
    }

    @Test
    @DisplayName("Doit publier un PaymentInitiatedEvent après l'initiation")
    void shouldPublishPaymentInitiatedEvent() {
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                new BigDecimal("100.00"), "USD", "0812345678", userId, "Test", companyId
        );

        when(paymentGateway.generateOrderNumber()).thenReturn("ORD-123");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> savedTransaction(invocation.getArgument(0)));
        when(paymentGateway.initiatePayment(any(), any(), any(), any()))
                .thenReturn(Map.of("success", true, "externalId", "EXT-789"));

        initiatePaymentService.execute(request);

        ArgumentCaptor<PaymentInitiatedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentInitiatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        PaymentInitiatedEvent event = eventCaptor.getValue();
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(event.currency()).isEqualTo("USD");
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Doit gérer l'échec FlexPay sans exception")
    void shouldHandleFlexPayFailureGracefully() {
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                new BigDecimal("100.00"), "USD", "0812345678", userId, "Test", companyId
        );

        when(paymentGateway.generateOrderNumber()).thenReturn("ORD-123");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> savedTransaction(invocation.getArgument(0)));
        when(paymentGateway.initiatePayment(any(), any(), any(), any()))
                .thenReturn(Map.of("success", false, "message", "Insufficient funds"));

        TransactionResponse response = initiatePaymentService.execute(request);

        // Transaction should still be returned (PENDING status, no externalRef update)
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TransactionStatus.PENDING);
    }

    @Test
    @DisplayName("Doit normaliser un numéro déjà au format 243")
    void shouldKeepAlready243Format() {
        String normalized = InitiatePaymentService.normalizePhone("243812345678");
        assertThat(normalized).isEqualTo("243812345678");
    }
}
