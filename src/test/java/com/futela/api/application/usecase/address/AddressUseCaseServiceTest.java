package com.futela.api.application.usecase.address;

import com.futela.api.application.dto.request.address.*;
import com.futela.api.application.dto.response.address.*;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.address.*;
import com.futela.api.domain.port.out.address.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressUseCaseServiceTest {

    @Mock
    private CountryRepositoryPort countryRepository;

    @Mock
    private ProvinceRepositoryPort provinceRepository;

    @Mock
    private CityRepositoryPort cityRepository;

    @Mock
    private TownRepositoryPort townRepository;

    @Mock
    private DistrictRepositoryPort districtRepository;

    @Mock
    private AddressRepositoryPort addressRepository;

    @InjectMocks
    private AddressUseCaseService service;

    private UUID countryId;
    private UUID provinceId;
    private UUID cityId;
    private UUID townId;
    private UUID districtId;

    @BeforeEach
    void setUp() {
        countryId = UUID.randomUUID();
        provinceId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        townId = UUID.randomUUID();
        districtId = UUID.randomUUID();
    }

    // === Country ===

    @Test
    @DisplayName("Doit creer un pays avec succes")
    void shouldCreateCountrySuccessfully() {
        var request = new CreateCountryRequest("Congo", "CD", "+243");
        var savedCountry = new Country(countryId, "Congo", "CD", "+243", true, Instant.now(), Instant.now());

        when(countryRepository.existsByCode("CD")).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        CountryResponse response = service.createCountry(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(countryId);
        assertThat(response.name()).isEqualTo("Congo");
        assertThat(response.code()).isEqualTo("CD");
        verify(countryRepository).save(any(Country.class));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'un pays avec un code duplique")
    void shouldRejectCreateCountryWithDuplicateCode() {
        var request = new CreateCountryRequest("Congo", "CD", "+243");

        when(countryRepository.existsByCode("CD")).thenReturn(true);

        assertThatThrownBy(() -> service.createCountry(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("CD");

        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit retourner la liste triee des pays")
    void shouldReturnCountriesList() {
        var country1 = new Country(UUID.randomUUID(), "Angola", "AO", "+244", true, Instant.now(), Instant.now());
        var country2 = new Country(UUID.randomUUID(), "Congo", "CD", "+243", true, Instant.now(), Instant.now());

        when(countryRepository.findAll()).thenReturn(List.of(country1, country2));

        List<CountryResponse> result = service.getCountries();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Angola");
        assertThat(result.get(1).name()).isEqualTo("Congo");
    }

    // === Province ===

    @Test
    @DisplayName("Doit creer une province avec succes")
    void shouldCreateProvinceSuccessfully() {
        var request = new CreateProvinceRequest("Kinshasa", "KIN", countryId);
        var country = new Country(countryId, "Congo", "CD", "+243", true, Instant.now(), Instant.now());
        var savedProvince = new Province(provinceId, "Kinshasa", "KIN", true, countryId, "Congo", Instant.now(), Instant.now());

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(provinceRepository.save(any(Province.class))).thenReturn(savedProvince);

        ProvinceResponse response = service.createProvince(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(provinceId);
        assertThat(response.name()).isEqualTo("Kinshasa");
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'une province avec un pays inexistant")
    void shouldRejectCreateProvinceWithNonExistentCountry() {
        var request = new CreateProvinceRequest("Kinshasa", "KIN", countryId);

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createProvince(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pays");

        verify(provinceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit retourner les provinces filtrees par pays")
    void shouldReturnProvincesByCountry() {
        var province1 = new Province(UUID.randomUUID(), "Kinshasa", "KIN", true, countryId, "Congo", Instant.now(), Instant.now());
        var province2 = new Province(UUID.randomUUID(), "Katanga", "KAT", true, countryId, "Congo", Instant.now(), Instant.now());

        when(provinceRepository.findByCountryId(countryId)).thenReturn(List.of(province1, province2));

        List<ProvinceResponse> result = service.getProvincesByCountry(countryId);

        assertThat(result).hasSize(2);
        verify(provinceRepository).findByCountryId(countryId);
    }

    // === City ===

    @Test
    @DisplayName("Doit creer une ville avec succes")
    void shouldCreateCitySuccessfully() {
        var request = new CreateCityRequest("Lubumbashi", "1234", provinceId);
        var province = new Province(provinceId, "Katanga", "KAT", true, countryId, "Congo", Instant.now(), Instant.now());
        var savedCity = new City(cityId, "Lubumbashi", "1234", true, provinceId, "Katanga", Instant.now(), Instant.now());

        when(provinceRepository.findById(provinceId)).thenReturn(Optional.of(province));
        when(cityRepository.save(any(City.class))).thenReturn(savedCity);

        CityResponse response = service.createCity(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(cityId);
        assertThat(response.name()).isEqualTo("Lubumbashi");
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'une ville avec une province inexistante")
    void shouldRejectCreateCityWithNonExistentProvince() {
        var request = new CreateCityRequest("Lubumbashi", "1234", provinceId);

        when(provinceRepository.findById(provinceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCity(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Province");

        verify(cityRepository, never()).save(any());
    }

    // === Town ===

    @Test
    @DisplayName("Doit creer une commune avec succes")
    void shouldCreateTownSuccessfully() {
        var request = new CreateTownRequest("Kampemba", "5678", cityId);
        var city = new City(cityId, "Lubumbashi", "1234", true, provinceId, "Katanga", Instant.now(), Instant.now());
        var savedTown = new Town(townId, "Kampemba", "5678", true, cityId, "Lubumbashi", Instant.now(), Instant.now());

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(townRepository.save(any(Town.class))).thenReturn(savedTown);

        TownResponse response = service.createTown(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(townId);
        assertThat(response.name()).isEqualTo("Kampemba");
        verify(townRepository).save(any(Town.class));
    }

    // === District ===

    @Test
    @DisplayName("Doit creer un quartier avec succes quand le townId est fourni")
    void shouldCreateDistrictSuccessfully() {
        var request = new CreateDistrictRequest("Rwashi", null, townId);
        var savedDistrict = new District(districtId, "Rwashi", true, null, null, townId, "Kampemba", Instant.now(), Instant.now());

        when(districtRepository.save(any(District.class))).thenReturn(savedDistrict);

        DistrictResponse response = service.createDistrict(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(districtId);
        assertThat(response.name()).isEqualTo("Rwashi");
        verify(districtRepository).save(any(District.class));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'un quartier sans ville ni commune")
    void shouldRejectCreateDistrictWithoutCityOrTown() {
        var request = new CreateDistrictRequest("Rwashi", null, null);

        assertThatThrownBy(() -> service.createDistrict(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("ville ou une commune");

        verify(districtRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit supprimer un quartier avec succes")
    void shouldDeleteDistrictSuccessfully() {
        var district = new District(districtId, "Rwashi", true, null, null, townId, "Kampemba", Instant.now(), Instant.now());

        when(districtRepository.findById(districtId)).thenReturn(Optional.of(district));

        service.deleteDistrict(districtId);

        verify(districtRepository).deleteById(districtId);
    }

    @Test
    @DisplayName("Doit rejeter la suppression d'un quartier inexistant")
    void shouldRejectDeleteNonExistentDistrict() {
        when(districtRepository.findById(districtId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteDistrict(districtId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quartier");

        verify(districtRepository, never()).deleteById(any());
    }

    // === Search Addresses ===

    @Test
    @DisplayName("Doit retourner les adresses correspondant a la recherche")
    void shouldReturnSearchResults() {
        var address = new Address(UUID.randomUUID(), "Avenue Lumumba", "10", null,
                -11.66, 27.48, null, null, townId, "Kampemba",
                cityId, "Lubumbashi", provinceId, "Katanga", countryId, "Congo",
                Instant.now(), Instant.now());

        when(addressRepository.search("Lumumba")).thenReturn(List.of(address));

        List<AddressResponse> result = service.searchAddresses("Lumumba");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().street()).isEqualTo("Avenue Lumumba");
        verify(addressRepository).search("Lumumba");
    }
}
