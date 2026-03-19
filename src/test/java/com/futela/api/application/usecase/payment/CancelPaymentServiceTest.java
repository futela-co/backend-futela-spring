package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
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
class CancelPaymentServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private CancelPaymentService cancelPaymentService;

    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
    }

    private Transaction createTransaction(TransactionStatus status) {
        return new Transaction(
                transactionId, "ORD-001", "EXT-001",
                TransactionType.PAYMENT, status,
                new BigDecimal("100.00"), "USD",
                "243812345678", null, UUID.randomUUID(), null,
                "Paiement", Map.of(), null, null,
                UUID.randomUUID(), Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit annuler une transaction PENDING avec succès")
    void shouldCancelPendingTransaction() {
        Transaction pendingTx = createTransaction(TransactionStatus.PENDING);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(pendingTx));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = cancelPaymentService.execute(transactionId);

        assertThat(response.status()).isEqualTo(TransactionStatus.CANCELLED);
        assertThat(captor.getValue().status()).isEqualTo(TransactionStatus.CANCELLED);
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour une transaction COMPLETED")
    void shouldThrowWhenCancellingCompletedTransaction() {
        Transaction completedTx = createTransaction(TransactionStatus.COMPLETED);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(completedTx));

        assertThatThrownBy(() -> cancelPaymentService.execute(transactionId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("déjà complétée");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour une transaction déjà CANCELLED")
    void shouldThrowWhenTransactionAlreadyCancelled() {
        Transaction cancelledTx = createTransaction(TransactionStatus.CANCELLED);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(cancelledTx));

        assertThatThrownBy(() -> cancelPaymentService.execute(transactionId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("déjà annulée");

        verify(transactionRepository, never()).save(any());
    }
}
