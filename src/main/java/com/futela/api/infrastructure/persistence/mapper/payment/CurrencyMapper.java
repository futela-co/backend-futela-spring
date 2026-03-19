package com.futela.api.infrastructure.persistence.mapper.payment;

import com.futela.api.domain.model.payment.Currency;
import com.futela.api.infrastructure.persistence.entity.payment.CurrencyEntity;

public final class CurrencyMapper {

    private CurrencyMapper() {}

    public static Currency toDomain(CurrencyEntity entity) {
        return new Currency(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getSymbol(),
                entity.getExchangeRate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
