package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class CleanupPendingPaymentsScheduler {

    private static final Logger log = LoggerFactory.getLogger(CleanupPendingPaymentsScheduler.class);

    private final TransactionRepositoryPort transactionRepository;

    public CleanupPendingPaymentsScheduler(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(cron = "${scheduler.cleanup-pending:0 0 2 * * *}")
    @Transactional
    public void cleanupOldPendingTransactions() {
        log.info("[Scheduler] Cleaning up old pending transactions...");

        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        var oldPending = transactionRepository.findPendingBefore(cutoff);
        int cancelled = 0;

        for (var tx : oldPending) {
            try {
                Transaction updated = new Transaction(
                        tx.id(), tx.reference(), tx.externalRef(),
                        tx.type(), TransactionStatus.CANCELLED,
                        tx.amount(), tx.currency(), tx.phoneNumber(), tx.provider(),
                        tx.userId(), tx.userName(), tx.description(), tx.metadata(),
                        "Transaction expirée après 24h", Instant.now(),
                        tx.companyId(), tx.createdAt(), tx.updatedAt()
                );
                transactionRepository.save(updated);
                cancelled++;
            } catch (Exception e) {
                log.error("[Scheduler] Error cancelling transaction {}: {}", tx.id(), e.getMessage());
            }
        }

        log.info("[Scheduler] Cleanup complete: {} transactions cancelled", cancelled);
    }
}
