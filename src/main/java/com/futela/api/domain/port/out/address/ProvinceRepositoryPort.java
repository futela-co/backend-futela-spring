package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.Province;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvinceRepositoryPort {
    List<Province> findByCountryId(UUID countryId);
    Optional<Province> findById(UUID id);
    Province save(Province province);
}
