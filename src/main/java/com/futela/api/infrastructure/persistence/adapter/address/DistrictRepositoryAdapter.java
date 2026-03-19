package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.model.address.District;
import com.futela.api.domain.port.out.address.DistrictRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.DistrictEntity;
import com.futela.api.infrastructure.persistence.mapper.address.DistrictPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.JpaCityRepository;
import com.futela.api.infrastructure.persistence.repository.address.JpaDistrictRepository;
import com.futela.api.infrastructure.persistence.repository.address.JpaTownRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DistrictRepositoryAdapter implements DistrictRepositoryPort {

    private final JpaDistrictRepository jpaRepository;
    private final JpaCityRepository cityRepository;
    private final JpaTownRepository townRepository;

    public DistrictRepositoryAdapter(JpaDistrictRepository jpaRepository,
                                     JpaCityRepository cityRepository,
                                     JpaTownRepository townRepository) {
        this.jpaRepository = jpaRepository;
        this.cityRepository = cityRepository;
        this.townRepository = townRepository;
    }

    @Override
    public List<District> findByTownId(UUID townId) {
        return jpaRepository.findByTownIdAndDeletedAtIsNullOrderByNameAsc(townId).stream()
                .map(DistrictPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<District> findByCityId(UUID cityId) {
        return jpaRepository.findByCityIdAndDeletedAtIsNullOrderByNameAsc(cityId).stream()
                .map(DistrictPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<District> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(DistrictPersistenceMapper::toDomain);
    }

    @Override
    public District save(District district) {
        DistrictEntity entity = new DistrictEntity();
        entity.setName(district.name());
        entity.setActive(district.isActive());

        if (district.cityId() != null) {
            entity.setCity(cityRepository.findById(district.cityId()).orElse(null));
        }
        if (district.townId() != null) {
            entity.setTown(townRepository.findById(district.townId()).orElse(null));
        }

        return DistrictPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
