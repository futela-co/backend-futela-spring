package com.futela.api.infrastructure.persistence.mapper.reservation;

import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.model.reservation.Reservation;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;

public final class ReservationPersistenceMapper {

    private ReservationPersistenceMapper() {}

    public static Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getUser().getId(),
                entity.getHost().getId(),
                entity.getCompany().getId(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getTotalPrice(),
                entity.getCurrency(),
                entity.getGuestCount(),
                entity.getNotes(),
                entity.getCancelReason(),
                entity.getConfirmedAt(),
                entity.getCancelledAt(),
                entity.getCompletedAt(),
                entity.getPaymentTransactionId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static void updateEntity(ReservationEntity entity, Reservation domain) {
        entity.setStatus(domain.status());
        entity.setStartDate(domain.startDate());
        entity.setEndDate(domain.endDate());
        entity.setTotalPrice(domain.totalPrice());
        entity.setCurrency(domain.currency());
        entity.setGuestCount(domain.guestCount());
        entity.setNotes(domain.notes());
        entity.setCancelReason(domain.cancelReason());
        entity.setConfirmedAt(domain.confirmedAt());
        entity.setCancelledAt(domain.cancelledAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setPaymentTransactionId(domain.paymentTransactionId());
    }
}
