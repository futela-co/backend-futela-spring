package com.futela.api.infrastructure.persistence.adapter.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.payment.TransactionEntity;
import com.futela.api.infrastructure.persistence.mapper.payment.TransactionMapper;
import com.futela.api.infrastructure.persistence.repository.payment.JpaTransactionRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository jpaRepository;

    public TransactionRepositoryAdapter(JpaTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity;
        if (transaction.id() != null) {
            entity = jpaRepository.findById(transaction.id()).orElse(new TransactionEntity());
        } else {
            entity = new TransactionEntity();
        }
        entity.setReference(transaction.reference());
        entity.setExternalRef(transaction.externalRef());
        entity.setType(transaction.type());
        entity.setStatus(transaction.status());
        entity.setAmount(transaction.amount());
        entity.setCurrency(transaction.currency());
        entity.setPhoneNumber(transaction.phoneNumber());
        entity.setProvider(transaction.provider());
        entity.setDescription(transaction.description());
        entity.setMetadata(transaction.metadata());
        entity.setFailureReason(transaction.failureReason());
        entity.setProcessedAt(transaction.processedAt());
        return TransactionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(TransactionMapper::toDomain);
    }

    @Override
    public Optional<Transaction> findByReference(String reference) {
        return jpaRepository.findByReference(reference).map(TransactionMapper::toDomain);
    }

    @Override
    public Optional<Transaction> findByExternalRef(String externalRef) {
        return jpaRepository.findByExternalRef(externalRef).map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(TransactionMapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return jpaRepository.findByStatusAndDeletedAtIsNull(status).stream()
                .map(TransactionMapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findPendingBefore(Instant cutoff) {
        return jpaRepository.findByStatusAndCreatedAtBeforeAndDeletedAtIsNull(TransactionStatus.PENDING, cutoff).stream()
                .map(TransactionMapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findByUserIdAndStatus(UUID userId, TransactionStatus status) {
        return jpaRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, status).stream()
                .map(TransactionMapper::toDomain).toList();
    }
}
