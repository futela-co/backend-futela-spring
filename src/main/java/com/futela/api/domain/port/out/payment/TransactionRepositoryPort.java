package com.futela.api.domain.port.out.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.model.payment.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    Optional<Transaction> findByReference(String reference);
    Optional<Transaction> findByExternalRef(String externalRef);
    List<Transaction> findByUserId(UUID userId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findPendingBefore(Instant cutoff);
    List<Transaction> findByUserIdAndStatus(UUID userId, TransactionStatus status);

    long countActive();
}
