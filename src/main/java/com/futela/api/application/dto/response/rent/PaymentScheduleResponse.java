package com.futela.api.application.dto.response.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.model.rent.PaymentSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentScheduleResponse(
        UUID id,
        UUID leaseId,
        LocalDate dueDate,
        BigDecimal amount,
        PaymentStatus status,
        UUID invoiceId
) {
    public static PaymentScheduleResponse from(PaymentSchedule schedule) {
        return new PaymentScheduleResponse(
                schedule.id(),
                schedule.leaseId(),
                schedule.dueDate(),
                schedule.amount(),
                schedule.status(),
                schedule.invoiceId()
        );
    }
}
