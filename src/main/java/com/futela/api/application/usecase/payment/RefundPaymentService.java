package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.RefundPaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.event.payment.PaymentRefundedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.in.payment.RefundPaymentUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class RefundPaymentService implements RefundPaymentUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RefundPaymentService(TransactionRepositoryPort transactionRepository,
                                ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TransactionResponse execute(RefundPaymentRequest request) {
        Transaction original = transactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", request.transactionId().toString()));

        if (original.status() != TransactionStatus.COMPLETED) {
            throw new InvalidOperationException("Seules les transactions complétées peuvent être remboursées");
        }

        String refundRef = "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Transaction refund = new Transaction(
                null, refundRef, null,
                TransactionType.REFUND, TransactionStatus.PENDING,
                original.amount(), original.currency(),
                original.phoneNumber(), original.provider(),
                original.userId(), original.userName(),
                "Remboursement de " + original.reference(),
                Map.of("originalTransactionId", original.id().toString()),
                null, null,
                original.companyId(), null, null
        );

        Transaction saved = transactionRepository.save(refund);
        eventPublisher.publishEvent(new PaymentRefundedEvent(original.id(), saved.id()));
        return TransactionResponse.from(saved);
    }
}
