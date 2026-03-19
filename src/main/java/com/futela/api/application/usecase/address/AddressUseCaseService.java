package com.futela.api.application.usecase.address;

import com.futela.api.application.dto.request.address.*;
import com.futela.api.application.dto.response.address.*;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.*;
import com.futela.api.domain.port.out.address.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AddressUseCaseService {

    private final CountryRepositoryPort countryRepository;
    private final ProvinceRepositoryPort provinceRepository;
    private final CityRepositoryPort cityRepository;
    private final TownRepositoryPort townRepository;
    private final DistrictRepositoryPort districtRepository;
    private final AddressRepositoryPort addressRepository;

    public AddressUseCaseService(
            CountryRepositoryPort countryRepository,
            ProvinceRepositoryPort provinceRepository,
            CityRepositoryPort cityRepository,
            TownRepositoryPort townRepository,
            DistrictRepositoryPort districtRepository,
            AddressRepositoryPort addressRepository) {
        this.countryRepository = countryRepository;
        this.provinceRepository = provinceRepository;
        this.cityRepository = cityRepository;
        this.townRepository = townRepository;
        this.districtRepository = districtRepository;
        this.addressRepository = addressRepository;
    }

    // === Country ===

    public CountryResponse createCountry(CreateCountryRequest request) {
        if (countryRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Un pays avec le code '" + request.code() + "' existe déjà");
        }
        var country = new Country(null, request.name(), request.code().toUpperCase(),
                request.phoneCode(), true, Instant.now(), Instant.now());
        return CountryResponse.fromDomain(countryRepository.save(country));
    }

    @Transactional(readOnly = true)
    public List<CountryResponse> getCountries() {
        return countryRepository.findAll().stream()
                .map(CountryResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public CountryResponse getCountryById(UUID id) {
        return countryRepository.findById(id)
                .map(CountryResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Pays", id.toString()));
    }

    public CountryResponse updateCountry(UUID id, UpdateCountryRequest request) {
        var existing = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pays", id.toString()));
        var updated = new Country(existing.id(), request.name(), existing.code(),
                request.phoneCode(), existing.isActive(), existing.createdAt(), Instant.now());
        return CountryResponse.fromDomain(countryRepository.save(updated));
    }

    // === Province ===

    public ProvinceResponse createProvince(CreateProvinceRequest request) {
        countryRepository.findById(request.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Pays", request.countryId().toString()));
        var province = new Province(null, request.name(), request.code(), true,
                request.countryId(), null, Instant.now(), Instant.now());
        return ProvinceResponse.fromDomain(provinceRepository.save(province));
    }

    @Transactional(readOnly = true)
    public List<ProvinceResponse> getProvincesByCountry(UUID countryId) {
        return provinceRepository.findByCountryId(countryId).stream()
                .map(ProvinceResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProvinceResponse getProvinceById(UUID id) {
        return provinceRepository.findById(id)
                .map(ProvinceResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Province", id.toString()));
    }

    public ProvinceResponse updateProvince(UUID id, UpdateProvinceRequest request) {
        var existing = provinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", id.toString()));
        var updated = new Province(existing.id(), request.name(), request.code(),
                existing.isActive(), existing.countryId(), existing.countryName(),
                existing.createdAt(), Instant.now());
        return ProvinceResponse.fromDomain(provinceRepository.save(updated));
    }

    // === City ===

    public CityResponse createCity(CreateCityRequest request) {
        provinceRepository.findById(request.provinceId())
                .orElseThrow(() -> new ResourceNotFoundException("Province", request.provinceId().toString()));
        var city = new City(null, request.name(), request.zipCode(), true,
                request.provinceId(), null, Instant.now(), Instant.now());
        return CityResponse.fromDomain(cityRepository.save(city));
    }

    @Transactional(readOnly = true)
    public List<CityResponse> getCitiesByProvince(UUID provinceId) {
        return cityRepository.findByProvinceId(provinceId).stream()
                .map(CityResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public CityResponse getCityById(UUID id) {
        return cityRepository.findById(id)
                .map(CityResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Ville", id.toString()));
    }

    public CityResponse updateCity(UUID id, UpdateCityRequest request) {
        var existing = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville", id.toString()));
        var updated = new City(existing.id(), request.name(), request.zipCode(),
                existing.isActive(), existing.provinceId(), existing.provinceName(),
                existing.createdAt(), Instant.now());
        return CityResponse.fromDomain(cityRepository.save(updated));
    }

    // === Town ===

    public TownResponse createTown(CreateTownRequest request) {
        cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville", request.cityId().toString()));
        var town = new Town(null, request.name(), request.zipCode(), true,
                request.cityId(), null, Instant.now(), Instant.now());
        return TownResponse.fromDomain(townRepository.save(town));
    }

    @Transactional(readOnly = true)
    public List<TownResponse> getTownsByCity(UUID cityId) {
        return townRepository.findByCityId(cityId).stream()
                .map(TownResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public TownResponse getTownById(UUID id) {
        return townRepository.findById(id)
                .map(TownResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Commune", id.toString()));
    }

    public TownResponse updateTown(UUID id, UpdateTownRequest request) {
        var existing = townRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commune", id.toString()));
        var updated = new Town(existing.id(), request.name(), request.zipCode(),
                existing.isActive(), existing.cityId(), existing.cityName(),
                existing.createdAt(), Instant.now());
        return TownResponse.fromDomain(townRepository.save(updated));
    }

    // === District ===

    public DistrictResponse createDistrict(CreateDistrictRequest request) {
        if (request.cityId() == null && request.townId() == null) {
            throw new InvalidOperationException("Le quartier doit appartenir à une ville ou une commune");
        }
        var district = new District(null, request.name(), true,
                request.cityId(), null, request.townId(), null,
                Instant.now(), Instant.now());
        return DistrictResponse.fromDomain(districtRepository.save(district));
    }

    @Transactional(readOnly = true)
    public List<DistrictResponse> getDistrictsByTown(UUID townId) {
        return districtRepository.findByTownId(townId).stream()
                .map(DistrictResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DistrictResponse> getDistrictsByCity(UUID cityId) {
        return districtRepository.findByCityId(cityId).stream()
                .map(DistrictResponse::fromDomain)
                .toList();
    }

    public void deleteDistrict(UUID id) {
        districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quartier", id.toString()));
        districtRepository.deleteById(id);
    }

    // === Address ===

    public AddressResponse createAddress(CreateAddressRequest request) {
        townRepository.findById(request.townId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune", request.townId().toString()));
        var address = new Address(null, request.street(), request.number(), request.additionalInfo(),
                request.latitude(), request.longitude(),
                request.districtId(), null,
                request.townId(), null, null, null, null, null, null, null,
                Instant.now(), Instant.now());
        return AddressResponse.fromDomain(addressRepository.save(address));
    }

    public AddressResponse updateAddress(UUID id, UpdateAddressRequest request) {
        addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id.toString()));
        townRepository.findById(request.townId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune", request.townId().toString()));
        var address = new Address(id, request.street(), request.number(), request.additionalInfo(),
                request.latitude(), request.longitude(),
                request.districtId(), null,
                request.townId(), null, null, null, null, null, null, null,
                null, Instant.now());
        return AddressResponse.fromDomain(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> searchAddresses(String query) {
        return addressRepository.search(query).stream()
                .map(AddressResponse::fromDomain)
                .toList();
    }
}
