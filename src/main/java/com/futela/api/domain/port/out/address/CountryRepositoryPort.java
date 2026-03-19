package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.Country;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountryRepositoryPort {
    List<Country> findAll();
    Optional<Country> findById(UUID id);
    Country save(Country country);
    boolean existsByCode(String code);
}
