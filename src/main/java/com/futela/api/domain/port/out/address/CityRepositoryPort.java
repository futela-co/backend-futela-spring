package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.City;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepositoryPort {
    List<City> findByProvinceId(UUID provinceId);
    Optional<City> findById(UUID id);
    City save(City city);
}
