package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.District;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DistrictRepositoryPort {
    List<District> findByTownId(UUID townId);
    List<District> findByCityId(UUID cityId);
    Optional<District> findById(UUID id);
    District save(District district);
    void deleteById(UUID id);
}
