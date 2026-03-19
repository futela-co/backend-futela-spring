package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.City;
import com.futela.api.domain.port.out.address.CityRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.CityEntity;
import com.futela.api.infrastructure.persistence.entity.address.ProvinceEntity;
import com.futela.api.infrastructure.persistence.mapper.address.CityPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.JpaCityRepository;
import com.futela.api.infrastructure.persistence.repository.address.JpaProvinceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CityRepositoryAdapter implements CityRepositoryPort {

    private final JpaCityRepository jpaRepository;
    private final JpaProvinceRepository provinceRepository;

    public CityRepositoryAdapter(JpaCityRepository jpaRepository, JpaProvinceRepository provinceRepository) {
        this.jpaRepository = jpaRepository;
        this.provinceRepository = provinceRepository;
    }

    @Override
    public List<City> findByProvinceId(UUID provinceId) {
        return jpaRepository.findByProvinceIdAndDeletedAtIsNullOrderByNameAsc(provinceId).stream()
                .map(CityPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<City> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(CityPersistenceMapper::toDomain);
    }

    @Override
    public City save(City city) {
        CityEntity entity;
        if (city.id() != null) {
            entity = jpaRepository.findById(city.id()).orElse(new CityEntity());
        } else {
            entity = new CityEntity();
        }
        entity.setName(city.name());
        entity.setZipCode(city.zipCode());
        entity.setActive(city.isActive());

        ProvinceEntity province = provinceRepository.findById(city.provinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Province", city.provinceId().toString()));
        entity.setProvince(province);

        return CityPersistenceMapper.toDomain(jpaRepository.save(entity));
    }
}
