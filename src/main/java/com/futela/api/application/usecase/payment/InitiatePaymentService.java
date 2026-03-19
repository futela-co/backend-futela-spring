package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.enums.TransactionType;
import com.futela.api.domain.event.payment.PaymentInitiatedEvent;
import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.domain.port.in.payment.InitiatePaymentUseCase;
import com.futela.api.domain.port.out.common.PaymentGatewayPort;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class InitiatePaymentService implements InitiatePaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(InitiatePaymentService.class);

    private final TransactionRepositoryPort transactionRepository;
    private final PaymentGatewayPort paymentGateway;
    private final ApplicationEventPublisher eventPublisher;

    public InitiatePaymentService(TransactionRepositoryPort transactionRepository,
                                  PaymentGatewayPort paymentGateway,
                                  ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.paymentGateway = paymentGateway;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TransactionResponse execute(InitiatePaymentRequest request) {
        String normalizedPhone = normalizePhone(request.phoneNumber());
        String reference = paymentGateway.generateOrderNumber();

        // Persist transaction BEFORE FlexPay call
        Transaction transaction = new Transaction(
                null, reference, null,
                TransactionType.PAYMENT, TransactionStatus.PENDING,
                request.amount(), request.currency(),
                normalizedPhone, null,
                request.userId(), null,
                request.description(),
                Map.of(), null, null,
                request.companyId(), null, null
        );

        Transaction saved = transactionRepository.save(transaction);

        // Call FlexPay
        Map<String, Object> result = paymentGateway.initiatePayment(
                request.amount(), request.currency(), normalizedPhone, reference
        );

        boolean success = Boolean.TRUE.equals(result.get("success"));
        String externalId = (String) result.get("externalId");

        if (success && externalId != null) {
            Transaction updated = new Transaction(
                    saved.id(), saved.reference(), externalId,
                    saved.type(), saved.status(), saved.amount(), saved.currency(),
                    saved.phoneNumber(), saved.provider(), saved.userId(), saved.userName(),
                    saved.description(), saved.metadata(), null, null,
                    saved.companyId(), saved.createdAt(), saved.updatedAt()
            );
            saved = transactionRepository.save(updated);
        } else {
            log.warn("[FlexPay] Initiation failed: {}", result.get("message"));
        }

        eventPublisher.publishEvent(new PaymentInitiatedEvent(
                saved.id(), saved.amount(), saved.currency(), saved.userId()
        ));

        return TransactionResponse.from(saved);
    }

    /**
     * Normalize phone number: add 243 prefix for DRC numbers.
     */
    static String normalizePhone(String phone) {
        if (phone == null) return "";
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("0") && cleaned.length() >= 10) {
            return "243" + cleaned.substring(1);
        }
        if (cleaned.startsWith("243")) {
            return cleaned;
        }
        return "243" + cleaned;
    }
}
