package com.futela.api.domain.port.out.payment;

import com.futela.api.domain.model.payment.Currency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepositoryPort {
    Currency save(Currency currency);
    Optional<Currency> findById(UUID id);
    Optional<Currency> findByCode(String code);
    List<Currency> findAll();
    List<Currency> findActive();
    boolean existsByCode(String code);
}
