package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.RefundPaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.event.payment.PaymentRefundedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
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
class RefundPaymentServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RefundPaymentService refundPaymentService;

    private UUID originalTransactionId;

    @BeforeEach
    void setUp() {
        originalTransactionId = UUID.randomUUID();
    }

    private Transaction createTransaction(TransactionStatus status) {
        return new Transaction(
                originalTransactionId, "ORD-001", "EXT-001",
                TransactionType.PAYMENT, status,
                new BigDecimal("100.00"), "USD",
                "243812345678", null, UUID.randomUUID(), null,
                "Paiement loyer", Map.of(), null, Instant.now(),
                UUID.randomUUID(), Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit créer une transaction REFUND pour une transaction COMPLETED")
    void shouldCreateRefundForCompletedTransaction() {
        Transaction completedTx = createTransaction(TransactionStatus.COMPLETED);

        when(transactionRepository.findById(originalTransactionId)).thenReturn(Optional.of(completedTx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            return new Transaction(
                    UUID.randomUUID(), tx.reference(), tx.externalRef(),
                    tx.type(), tx.status(), tx.amount(), tx.currency(),
                    tx.phoneNumber(), tx.provider(), tx.userId(), tx.userName(),
                    tx.description(), tx.metadata(), tx.failureReason(), tx.processedAt(),
                    tx.companyId(), Instant.now(), Instant.now()
            );
        });

        RefundPaymentRequest request = new RefundPaymentRequest(originalTransactionId, "Erreur de paiement");

        TransactionResponse response = refundPaymentService.execute(request);

        assertThat(response.type()).isEqualTo(TransactionType.REFUND);
        assertThat(response.status()).isEqualTo(TransactionStatus.PENDING);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.description()).contains("Remboursement");
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour une transaction non COMPLETED")
    void shouldThrowWhenTransactionNotCompleted() {
        Transaction pendingTx = createTransaction(TransactionStatus.PENDING);

        when(transactionRepository.findById(originalTransactionId)).thenReturn(Optional.of(pendingTx));

        RefundPaymentRequest request = new RefundPaymentRequest(originalTransactionId, null);

        assertThatThrownBy(() -> refundPaymentService.execute(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("complétées");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit publier un PaymentRefundedEvent après le remboursement")
    void shouldPublishPaymentRefundedEvent() {
        Transaction completedTx = createTransaction(TransactionStatus.COMPLETED);

        when(transactionRepository.findById(originalTransactionId)).thenReturn(Optional.of(completedTx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            return new Transaction(
                    UUID.randomUUID(), tx.reference(), tx.externalRef(),
                    tx.type(), tx.status(), tx.amount(), tx.currency(),
                    tx.phoneNumber(), tx.provider(), tx.userId(), tx.userName(),
                    tx.description(), tx.metadata(), tx.failureReason(), tx.processedAt(),
                    tx.companyId(), Instant.now(), Instant.now()
            );
        });

        RefundPaymentRequest request = new RefundPaymentRequest(originalTransactionId, null);

        refundPaymentService.execute(request);

        ArgumentCaptor<PaymentRefundedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentRefundedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        PaymentRefundedEvent event = eventCaptor.getValue();
        assertThat(event.originalTransactionId()).isEqualTo(originalTransactionId);
        assertThat(event.refundTransactionId()).isNotNull();
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour une transaction FAILED")
    void shouldThrowWhenTransactionFailed() {
        Transaction failedTx = createTransaction(TransactionStatus.FAILED);

        when(transactionRepository.findById(originalTransactionId)).thenReturn(Optional.of(failedTx));

        RefundPaymentRequest request = new RefundPaymentRequest(originalTransactionId, null);

        assertThatThrownBy(() -> refundPaymentService.execute(request))
                .isInstanceOf(InvalidOperationException.class);
    }
}
