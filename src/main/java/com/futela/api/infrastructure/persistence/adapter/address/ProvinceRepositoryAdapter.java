package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.Province;
import com.futela.api.domain.port.out.address.ProvinceRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.CountryEntity;
import com.futela.api.infrastructure.persistence.entity.address.ProvinceEntity;
import com.futela.api.infrastructure.persistence.mapper.address.ProvincePersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.JpaCountryRepository;
import com.futela.api.infrastructure.persistence.repository.address.JpaProvinceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProvinceRepositoryAdapter implements ProvinceRepositoryPort {

    private final JpaProvinceRepository jpaRepository;
    private final JpaCountryRepository countryRepository;

    public ProvinceRepositoryAdapter(JpaProvinceRepository jpaRepository, JpaCountryRepository countryRepository) {
        this.jpaRepository = jpaRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Province> findByCountryId(UUID countryId) {
        return jpaRepository.findByCountryIdAndDeletedAtIsNullOrderByNameAsc(countryId).stream()
                .map(ProvincePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Province> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(ProvincePersistenceMapper::toDomain);
    }

    @Override
    public Province save(Province province) {
        ProvinceEntity entity;
        if (province.id() != null) {
            entity = jpaRepository.findById(province.id()).orElse(new ProvinceEntity());
        } else {
            entity = new ProvinceEntity();
        }
        entity.setName(province.name());
        entity.setCode(province.code());
        entity.setActive(province.isActive());

        CountryEntity country = countryRepository.findById(province.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Pays", province.countryId().toString()));
        entity.setCountry(country);

        return ProvincePersistenceMapper.toDomain(jpaRepository.save(entity));
    }
}
