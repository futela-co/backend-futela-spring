package com.futela.api.infrastructure.persistence.repository.payment;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.infrastructure.persistence.entity.payment.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findByDeletedAtIsNull(Pageable pageable);
    Optional<TransactionEntity> findByReference(String reference);
    Optional<TransactionEntity> findByExternalRef(String externalRef);
    List<TransactionEntity> findByUserIdAndDeletedAtIsNull(UUID userId);
    List<TransactionEntity> findByStatusAndDeletedAtIsNull(TransactionStatus status);
    List<TransactionEntity> findByStatusAndCreatedAtBeforeAndDeletedAtIsNull(TransactionStatus status, Instant cutoff);
    List<TransactionEntity> findByUserIdAndStatusAndDeletedAtIsNull(UUID userId, TransactionStatus status);

    long countByDeletedAtIsNull();
}
