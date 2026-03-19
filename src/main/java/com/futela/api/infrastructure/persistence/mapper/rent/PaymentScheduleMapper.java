package com.futela.api.infrastructure.persistence.mapper.rent;

import com.futela.api.domain.model.rent.PaymentSchedule;
import com.futela.api.infrastructure.persistence.entity.rent.PaymentScheduleEntity;

public final class PaymentScheduleMapper {

    private PaymentScheduleMapper() {}

    public static PaymentSchedule toDomain(PaymentScheduleEntity entity) {
        return new PaymentSchedule(
                entity.getId(),
                entity.getLease().getId(),
                entity.getDueDate(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getInvoiceId(),
                entity.getCompany().getId(),
                entity.getCreatedAt()
        );
    }
}
