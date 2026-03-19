package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.port.in.payment.ConfirmPaymentUseCase;
import com.futela.api.domain.port.out.common.PaymentGatewayPort;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlexPaySyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(FlexPaySyncScheduler.class);

    private final TransactionRepositoryPort transactionRepository;
    private final PaymentGatewayPort paymentGateway;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    public FlexPaySyncScheduler(TransactionRepositoryPort transactionRepository,
                                PaymentGatewayPort paymentGateway,
                                ConfirmPaymentUseCase confirmPaymentUseCase) {
        this.transactionRepository = transactionRepository;
        this.paymentGateway = paymentGateway;
        this.confirmPaymentUseCase = confirmPaymentUseCase;
    }

    @Scheduled(fixedRateString = "${scheduler.flexpay-sync-ms:300000}")
    public void syncPendingTransactions() {
        log.info("[Scheduler] Syncing pending FlexPay transactions...");

        var pending = transactionRepository.findByStatus(TransactionStatus.PENDING);
        int synced = 0;

        for (var tx : pending) {
            if (tx.externalRef() == null || tx.externalRef().isBlank()) continue;

            try {
                Map<String, Object> result = paymentGateway.verifyPayment(tx.externalRef());
                String status = (String) result.get("status");

                if ("completed".equals(status)) {
                    confirmPaymentUseCase.execute(tx.externalRef(), "completed");
                    synced++;
                }
            } catch (Exception e) {
                log.error("[Scheduler] Error syncing transaction {}: {}", tx.id(), e.getMessage());
            }
        }

        log.info("[Scheduler] FlexPay sync complete: {} transactions updated", synced);
    }
}
