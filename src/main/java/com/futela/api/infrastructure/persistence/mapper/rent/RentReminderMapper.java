package com.futela.api.infrastructure.persistence.mapper.rent;

import com.futela.api.domain.model.rent.RentReminder;
import com.futela.api.infrastructure.persistence.entity.rent.RentReminderEntity;

public final class RentReminderMapper {

    private RentReminderMapper() {}

    public static RentReminder toDomain(RentReminderEntity entity) {
        return new RentReminder(
                entity.getId(),
                entity.getInvoice().getId(),
                entity.getLease().getId(),
                entity.getType(),
                entity.getSentAt(),
                entity.getChannel(),
                entity.getCompany().getId(),
                entity.getCreatedAt()
        );
    }
}
