package com.futela.api.application.mapper.reservation;

import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;

public final class ReservationResponseMapper {

    private ReservationResponseMapper() {}

    public static ReservationResponse fromEntity(ReservationEntity entity) {
        return new ReservationResponse(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getProperty().getTitle(),
                entity.getUser().getId(),
                entity.getUser().getFirstName() + " " + entity.getUser().getLastName(),
                entity.getHost().getId(),
                entity.getStatus(),
                ReservationResponse.labelFor(entity.getStatus()),
                ReservationResponse.colorFor(entity.getStatus()),
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
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
