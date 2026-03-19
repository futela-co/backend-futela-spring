package com.futela.api.infrastructure.persistence.mapper.rent;

import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.infrastructure.persistence.entity.rent.RentInvoiceEntity;

public final class RentInvoiceMapper {

    private RentInvoiceMapper() {}

    public static RentInvoice toDomain(RentInvoiceEntity entity) {
        return new RentInvoice(
                entity.getId(),
                entity.getLease().getId(),
                entity.getInvoiceNumber(),
                entity.getAmount(),
                entity.getPaidAmount(),
                entity.getStatus(),
                entity.getDueDate(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getLateFee(),
                entity.getCompany().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
