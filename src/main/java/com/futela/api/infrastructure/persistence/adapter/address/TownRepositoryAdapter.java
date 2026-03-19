package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.Town;
import com.futela.api.domain.port.out.address.TownRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.CityEntity;
import com.futela.api.infrastructure.persistence.entity.address.TownEntity;
import com.futela.api.infrastructure.persistence.mapper.address.TownPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.JpaCityRepository;
import com.futela.api.infrastructure.persistence.repository.address.JpaTownRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TownRepositoryAdapter implements TownRepositoryPort {

    private final JpaTownRepository jpaRepository;
    private final JpaCityRepository cityRepository;

    public TownRepositoryAdapter(JpaTownRepository jpaRepository, JpaCityRepository cityRepository) {
        this.jpaRepository = jpaRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public List<Town> findByCityId(UUID cityId) {
        return jpaRepository.findByCityIdAndDeletedAtIsNullOrderByNameAsc(cityId).stream()
                .map(TownPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Town> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(TownPersistenceMapper::toDomain);
    }

    @Override
    public Town save(Town town) {
        TownEntity entity;
        if (town.id() != null) {
            entity = jpaRepository.findById(town.id()).orElse(new TownEntity());
        } else {
            entity = new TownEntity();
        }
        entity.setName(town.name());
        entity.setZipCode(town.zipCode());
        entity.setActive(town.isActive());

        CityEntity city = cityRepository.findById(town.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville", town.cityId().toString()));
        entity.setCity(city);

        return TownPersistenceMapper.toDomain(jpaRepository.save(entity));
    }
}
