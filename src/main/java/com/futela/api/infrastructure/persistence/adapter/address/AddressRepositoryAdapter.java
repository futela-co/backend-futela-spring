package com.futela.api.infrastructure.persistence.adapter.address;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.Address;
import com.futela.api.domain.port.out.address.AddressRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import com.futela.api.infrastructure.persistence.mapper.address.AddressPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.address.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AddressRepositoryAdapter implements AddressRepositoryPort {

    private final JpaAddressRepository jpaRepository;
    private final JpaTownRepository townRepository;
    private final JpaCityRepository cityRepository;
    private final JpaProvinceRepository provinceRepository;
    private final JpaCountryRepository countryRepository;
    private final JpaDistrictRepository districtRepository;

    public AddressRepositoryAdapter(JpaAddressRepository jpaRepository,
                                    JpaTownRepository townRepository,
                                    JpaCityRepository cityRepository,
                                    JpaProvinceRepository provinceRepository,
                                    JpaCountryRepository countryRepository,
                                    JpaDistrictRepository districtRepository) {
        this.jpaRepository = jpaRepository;
        this.townRepository = townRepository;
        this.cityRepository = cityRepository;
        this.provinceRepository = provinceRepository;
        this.countryRepository = countryRepository;
        this.districtRepository = districtRepository;
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(AddressPersistenceMapper::toDomain);
    }

    @Override
    public Address save(Address address) {
        AddressEntity entity;
        if (address.id() != null) {
            entity = jpaRepository.findById(address.id()).orElse(new AddressEntity());
        } else {
            entity = new AddressEntity();
        }
        entity.setStreet(address.street());
        entity.setNumber(address.number());
        entity.setAdditionalInfo(address.additionalInfo());
        entity.setLatitude(address.latitude());
        entity.setLongitude(address.longitude());

        var town = townRepository.findById(address.townId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune", address.townId().toString()));
        entity.setTown(town);
        entity.setCity(town.getCity());
        entity.setProvince(town.getCity().getProvince());
        entity.setCountry(town.getCity().getProvince().getCountry());

        if (address.districtId() != null) {
            entity.setDistrict(districtRepository.findById(address.districtId()).orElse(null));
        }

        return AddressPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Address> search(String query) {
        return jpaRepository.search(query).stream()
                .map(AddressPersistenceMapper::toDomain)
                .toList();
    }
}
