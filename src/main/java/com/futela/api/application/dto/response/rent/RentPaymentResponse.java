package com.futela.api.application.dto.response.rent;

import com.futela.api.domain.model.rent.RentPayment;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RentPaymentResponse(
        UUID id,
        UUID invoiceId,
        UUID leaseId,
        BigDecimal amount,
        LocalDate paymentDate,
        String paymentMethod,
        String reference,
        String notes,
        Instant createdAt
) {
    public static RentPaymentResponse from(RentPayment payment) {
        return new RentPaymentResponse(
                payment.id(),
                payment.invoiceId(),
                payment.leaseId(),
                payment.amount(),
                payment.paymentDate(),
                payment.paymentMethod(),
                payment.reference(),
                payment.notes(),
                payment.createdAt()
        );
    }
}
