package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.in.payment.CancelPaymentUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class CancelPaymentService implements CancelPaymentUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public CancelPaymentService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionResponse execute(UUID transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId.toString()));

        if (tx.status() == TransactionStatus.COMPLETED) {
            throw new InvalidOperationException("Impossible d'annuler une transaction déjà complétée");
        }
        if (tx.status() == TransactionStatus.CANCELLED) {
            throw new InvalidOperationException("Cette transaction est déjà annulée");
        }

        Transaction cancelled = new Transaction(
                tx.id(), tx.reference(), tx.externalRef(),
                tx.type(), TransactionStatus.CANCELLED,
                tx.amount(), tx.currency(), tx.phoneNumber(), tx.provider(),
                tx.userId(), tx.userName(), tx.description(), tx.metadata(),
                null, Instant.now(),
                tx.companyId(), tx.createdAt(), tx.updatedAt()
        );

        return TransactionResponse.from(transactionRepository.save(cancelled));
    }
}
