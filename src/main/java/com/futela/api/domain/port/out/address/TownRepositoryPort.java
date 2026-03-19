package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.Town;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TownRepositoryPort {
    List<Town> findByCityId(UUID cityId);
    Optional<Town> findById(UUID id);
    Town save(Town town);
}
