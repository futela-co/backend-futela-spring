package com.futela.api.infrastructure.persistence.adapter.payment;

import com.futela.api.domain.model.payment.Currency;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.payment.CurrencyEntity;
import com.futela.api.infrastructure.persistence.mapper.payment.CurrencyMapper;
import com.futela.api.infrastructure.persistence.repository.payment.JpaCurrencyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CurrencyRepositoryAdapter implements CurrencyRepositoryPort {

    private final JpaCurrencyRepository jpaRepository;

    public CurrencyRepositoryAdapter(JpaCurrencyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Currency save(Currency currency) {
        CurrencyEntity entity;
        if (currency.id() != null) {
            entity = jpaRepository.findById(currency.id()).orElse(new CurrencyEntity());
        } else {
            entity = new CurrencyEntity();
        }
        entity.setCode(currency.code().toUpperCase());
        entity.setName(currency.name());
        entity.setSymbol(currency.symbol());
        entity.setExchangeRate(currency.exchangeRate());
        entity.setActive(currency.isActive());
        return CurrencyMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Currency> findById(UUID id) {
        return jpaRepository.findById(id).map(CurrencyMapper::toDomain);
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return jpaRepository.findByCode(code.toUpperCase()).map(CurrencyMapper::toDomain);
    }

    @Override
    public List<Currency> findAll() {
        return jpaRepository.findAll().stream().map(CurrencyMapper::toDomain).toList();
    }

    @Override
    public List<Currency> findActive() {
        return jpaRepository.findByIsActiveTrue().stream().map(CurrencyMapper::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code.toUpperCase());
    }
}
