package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.event.payment.PaymentCompletedEvent;
import com.futela.api.domain.event.payment.PaymentFailedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.in.payment.ConfirmPaymentUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmPaymentService.class);

    private final TransactionRepositoryPort transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConfirmPaymentService(TransactionRepositoryPort transactionRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TransactionResponse execute(String externalRef, String status) {
        Transaction tx = transactionRepository.findByExternalRef(externalRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", externalRef));

        // Idempotence: ignore if already in final state
        if (tx.isFinal()) {
            log.info("Transaction {} already in final state {}", externalRef, tx.status());
            return TransactionResponse.from(tx);
        }

        TransactionStatus newStatus;
        String failureReason = null;

        if ("completed".equals(status) || "success".equals(status) || "0".equals(status)) {
            newStatus = TransactionStatus.COMPLETED;
        } else if ("failed".equals(status) || "2".equals(status)) {
            newStatus = TransactionStatus.FAILED;
            failureReason = "Paiement échoué depuis la passerelle";
        } else {
            newStatus = TransactionStatus.CANCELLED;
            failureReason = "Paiement annulé";
        }

        Transaction updated = new Transaction(
                tx.id(), tx.reference(), tx.externalRef(),
                tx.type(), newStatus,
                tx.amount(), tx.currency(), tx.phoneNumber(), tx.provider(),
                tx.userId(), tx.userName(), tx.description(), tx.metadata(),
                failureReason, Instant.now(),
                tx.companyId(), tx.createdAt(), tx.updatedAt()
        );

        Transaction saved = transactionRepository.save(updated);

        if (newStatus == TransactionStatus.COMPLETED) {
            eventPublisher.publishEvent(new PaymentCompletedEvent(saved.id(), externalRef));
        } else if (newStatus == TransactionStatus.FAILED) {
            eventPublisher.publishEvent(new PaymentFailedEvent(saved.id(), failureReason));
        }

        return TransactionResponse.from(saved);
    }
}
