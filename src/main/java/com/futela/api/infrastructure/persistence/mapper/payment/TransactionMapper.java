package com.futela.api.infrastructure.persistence.mapper.payment;

import com.futela.api.domain.model.payment.Transaction;
import com.futela.api.infrastructure.persistence.entity.payment.TransactionEntity;
import com.futela.api.infrastructure.persistence.mapper.property.PropertyPersistenceMapper;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getReference(),
                entity.getExternalRef(),
                entity.getType(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getPhoneNumber(),
                entity.getProvider(),
                entity.getUser().getId(),
                entity.getUser().getFirstName() + " " + entity.getUser().getLastName(),
                entity.getDescription(),
                PropertyPersistenceMapper.jsonNodeToMap(entity.getMetadata()),
                entity.getFailureReason(),
                entity.getProcessedAt(),
                entity.getCompany().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
