package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.model.address.Country;
import com.futela.api.domain.port.out.address.CountryRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.CountryEntity;
import com.futela.api.infrastructure.persistence.mapper.address.CountryPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.JpaCountryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CountryRepositoryAdapter implements CountryRepositoryPort {

    private final JpaCountryRepository jpaRepository;

    public CountryRepositoryAdapter(JpaCountryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Country> findAll() {
        return jpaRepository.findAllByDeletedAtIsNullOrderByNameAsc().stream()
                .map(CountryPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Country> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(CountryPersistenceMapper::toDomain);
    }

    @Override
    public Country save(Country country) {
        CountryEntity entity;
        if (country.id() != null) {
            entity = jpaRepository.findById(country.id()).orElse(new CountryEntity());
        } else {
            entity = new CountryEntity();
        }
        entity.setName(country.name());
        entity.setCode(country.code().toUpperCase());
        entity.setPhoneCode(country.phoneCode());
        entity.setActive(country.isActive());
        return CountryPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code.toUpperCase());
    }
}
