package com.futela.api.infrastructure.persistence.mapper.reservation;

import com.futela.api.domain.model.reservation.Visit;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;

public final class VisitPersistenceMapper {

    private VisitPersistenceMapper() {}

    public static Visit toDomain(VisitEntity entity) {
        return new Visit(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getUser().getId(),
                entity.getCompany().getId(),
                entity.getStatus(),
                entity.getScheduledAt(),
                entity.getNotes(),
                entity.getCancelReason(),
                entity.getConfirmedAt(),
                entity.getCompletedAt(),
                entity.getPaymentTransactionId(),
                entity.isPaid(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static void updateEntity(VisitEntity entity, Visit domain) {
        entity.setStatus(domain.status());
        entity.setScheduledAt(domain.scheduledAt());
        entity.setNotes(domain.notes());
        entity.setCancelReason(domain.cancelReason());
        entity.setConfirmedAt(domain.confirmedAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setPaymentTransactionId(domain.paymentTransactionId());
        entity.setPaid(domain.isPaid());
    }
}
