package com.futela.api.application.mapper.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;

public final class VisitResponseMapper {

    private VisitResponseMapper() {}

    public static VisitResponse fromEntity(VisitEntity entity) {
        return new VisitResponse(
                entity.getId(),
                entity.getProperty().getId(),
                entity.getProperty().getTitle(),
                entity.getUser().getId(),
                entity.getUser().getFirstName() + " " + entity.getUser().getLastName(),
                entity.getStatus(),
                VisitResponse.labelFor(entity.getStatus()),
                VisitResponse.colorFor(entity.getStatus()),
                entity.getScheduledAt(),
                entity.getNotes(),
                entity.getCancelReason(),
                entity.getConfirmedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
