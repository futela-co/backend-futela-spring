package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.event.payment.PaymentCompletedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Transaction;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ConfirmPaymentService confirmPaymentService;

    private UUID transactionId;
    private Transaction pendingTransaction;
    private String externalRef;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        externalRef = "EXT-123";

        pendingTransaction = new Transaction(
                transactionId, "ORD-001", externalRef,
                TransactionType.PAYMENT, TransactionStatus.PENDING,
                new BigDecimal("100.00"), "USD",
                "243812345678", null, UUID.randomUUID(), null,
                "Paiement loyer", Map.of(), null, null,
                UUID.randomUUID(), Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit confirmer une transaction PENDING en COMPLETED")
    void shouldConfirmPendingTransactionToCompleted() {
        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(pendingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = confirmPaymentService.execute(externalRef, "completed");

        assertThat(response.status()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("Doit être idempotent pour une transaction déjà COMPLETED")
    void shouldBeIdempotentForCompletedTransaction() {
        Transaction completedTx = new Transaction(
                transactionId, "ORD-001", externalRef,
                TransactionType.PAYMENT, TransactionStatus.COMPLETED,
                new BigDecimal("100.00"), "USD",
                "243812345678", null, UUID.randomUUID(), null,
                "Paiement", Map.of(), null, Instant.now(),
                UUID.randomUUID(), Instant.now(), Instant.now()
        );

        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(completedTx));

        TransactionResponse response = confirmPaymentService.execute(externalRef, "completed");

        assertThat(response.status()).isEqualTo(TransactionStatus.COMPLETED);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException pour une référence inexistante")
    void shouldThrowWhenExternalRefNotFound() {
        when(transactionRepository.findByExternalRef("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> confirmPaymentService.execute("UNKNOWN", "completed"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit publier un PaymentCompletedEvent en cas de succès")
    void shouldPublishPaymentCompletedEvent() {
        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(pendingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        confirmPaymentService.execute(externalRef, "success");

        ArgumentCaptor<PaymentCompletedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        PaymentCompletedEvent event = eventCaptor.getValue();
        assertThat(event.transactionId()).isEqualTo(transactionId);
        assertThat(event.externalRef()).isEqualTo(externalRef);
    }

    @Test
    @DisplayName("Doit marquer comme FAILED quand le statut est 'failed'")
    void shouldMarkAsFailedWhenStatusIsFailed() {
        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(pendingTransaction));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        confirmPaymentService.execute(externalRef, "failed");

        Transaction saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(TransactionStatus.FAILED);
        assertThat(saved.failureReason()).isNotNull();
    }

    @Test
    @DisplayName("Doit marquer comme CANCELLED pour un statut inconnu")
    void shouldMarkAsCancelledForUnknownStatus() {
        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(pendingTransaction));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        confirmPaymentService.execute(externalRef, "unknown_status");

        Transaction saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(TransactionStatus.CANCELLED);
    }

    @Test
    @DisplayName("Doit confirmer avec le code statut '0' (FlexPay)")
    void shouldConfirmWithStatusCode0() {
        when(transactionRepository.findByExternalRef(externalRef)).thenReturn(Optional.of(pendingTransaction));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        confirmPaymentService.execute(externalRef, "0");

        assertThat(captor.getValue().status()).isEqualTo(TransactionStatus.COMPLETED);
    }
}
