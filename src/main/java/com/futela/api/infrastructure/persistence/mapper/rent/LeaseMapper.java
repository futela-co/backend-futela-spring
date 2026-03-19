package com.futela.api.infrastructure.persistence.mapper.rent;

import com.futela.api.domain.model.rent.Lease;
import com.futela.api.infrastructure.persistence.entity.rent.LeaseEntity;

public final class LeaseMapper {

    private LeaseMapper() {}

    public static Lease toDomain(LeaseEntity entity) {
        return new Lease(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getProperty().getTitle(),
                entity.getTenant().getId(),
                entity.getTenant().getFirstName() + " " + entity.getTenant().getLastName(),
                entity.getLandlord().getId(),
                entity.getLandlord().getFirstName() + " " + entity.getLandlord().getLastName(),
                entity.getStatus(),
                entity.getMonthlyRent(),
                entity.getCurrency(),
                entity.getDepositAmount(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPaymentDayOfMonth(),
                entity.getNotes(),
                entity.getTerminatedAt(),
                entity.getTerminationReason(),
                entity.getCompany().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
