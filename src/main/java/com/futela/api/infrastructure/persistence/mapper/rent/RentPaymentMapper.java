package com.futela.api.infrastructure.persistence.mapper.rent;

import com.futela.api.domain.model.rent.RentPayment;
import com.futela.api.infrastructure.persistence.entity.rent.RentPaymentEntity;

public final class RentPaymentMapper {

    private RentPaymentMapper() {}

    public static RentPayment toDomain(RentPaymentEntity entity) {
        return new RentPayment(
                entity.getId(),
                entity.getInvoice() != null ? entity.getInvoice().getId() : null,
                entity.getLease().getId(),
                entity.getAmount(),
                entity.getPaymentDate(),
                entity.getPaymentMethod(),
                entity.getReference(),
                entity.getNotes(),
                entity.getCompany().getId(),
                entity.getCreatedAt()
        );
    }
}
