package com.futela.api.presentation.controller.address;

import com.futela.api.application.dto.request.address.*;
import com.futela.api.application.dto.response.address.*;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.usecase.address.AddressUseCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressUseCaseService addressService;

    public AddressController(AddressUseCaseService addressService) {
        this.addressService = addressService;
    }

    // === Countries ===
    // Symfony: GET /countries
    @GetMapping("/countries")
    public ApiResponse<List<CountryResponse>> getCountries() {
        return ApiResponse.success(addressService.getCountries());
    }

    // Symfony: GET /countries/{id}
    @GetMapping("/countries/{id}")
    public ApiResponse<CountryResponse> getCountryById(@PathVariable UUID id) {
        return ApiResponse.success(addressService.getCountryById(id));
    }

    @PostMapping("/admin/countries")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CountryResponse> createCountry(@Valid @RequestBody CreateCountryRequest request) {
        return ApiResponse.success(addressService.createCountry(request), "Pays créé avec succès");
    }

    @PutMapping("/admin/countries/{id}")
    public ApiResponse<CountryResponse> updateCountry(@PathVariable UUID id, @Valid @RequestBody UpdateCountryRequest request) {
        return ApiResponse.success(addressService.updateCountry(id, request), "Pays modifié avec succès");
    }

    // === Provinces ===
    // Symfony: GET /provinces?countryId={uuid}
    @GetMapping("/provinces")
    public ApiResponse<List<ProvinceResponse>> getProvinces(@RequestParam(required = false) UUID countryId) {
        if (countryId != null) {
            return ApiResponse.success(addressService.getProvincesByCountry(countryId));
        }
        return ApiResponse.success(addressService.getProvincesByCountry(null));
    }

    // Keep nested endpoint for backward compat
    @GetMapping("/countries/{id}/provinces")
    public ApiResponse<List<ProvinceResponse>> getProvincesByCountry(@PathVariable UUID id) {
        return ApiResponse.success(addressService.getProvincesByCountry(id));
    }

    @PostMapping("/admin/provinces")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProvinceResponse> createProvince(@Valid @RequestBody CreateProvinceRequest request) {
        return ApiResponse.success(addressService.createProvince(request), "Province créée avec succès");
    }

    @PutMapping("/admin/provinces/{id}")
    public ApiResponse<ProvinceResponse> updateProvince(@PathVariable UUID id, @Valid @RequestBody UpdateProvinceRequest request) {
        return ApiResponse.success(addressService.updateProvince(id, request), "Province modifiée avec succès");
    }

    // === Cities ===
    // Symfony: GET /cities?provinceId={uuid}
    @GetMapping("/cities")
    public ApiResponse<List<CityResponse>> getCities(@RequestParam(required = false) UUID provinceId) {
        if (provinceId != null) {
            return ApiResponse.success(addressService.getCitiesByProvince(provinceId));
        }
        return ApiResponse.success(addressService.getCitiesByProvince(null));
    }

    // Keep nested endpoint for backward compat
    @GetMapping("/provinces/{id}/cities")
    public ApiResponse<List<CityResponse>> getCitiesByProvince(@PathVariable UUID id) {
        return ApiResponse.success(addressService.getCitiesByProvince(id));
    }

    @PostMapping("/admin/cities")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CityResponse> createCity(@Valid @RequestBody CreateCityRequest request) {
        return ApiResponse.success(addressService.createCity(request), "Ville créée avec succès");
    }

    @PutMapping("/admin/cities/{id}")
    public ApiResponse<CityResponse> updateCity(@PathVariable UUID id, @Valid @RequestBody UpdateCityRequest request) {
        return ApiResponse.success(addressService.updateCity(id, request), "Ville modifiée avec succès");
    }

    // === Towns ===
    // Symfony: GET /towns?cityId={uuid}
    @GetMapping("/towns")
    public ApiResponse<List<TownResponse>> getTowns(@RequestParam(required = false) UUID cityId) {
        if (cityId != null) {
            return ApiResponse.success(addressService.getTownsByCity(cityId));
        }
        return ApiResponse.success(addressService.getTownsByCity(null));
    }

    // Keep nested endpoint for backward compat
    @GetMapping("/cities/{cityId}/towns")
    public ApiResponse<List<TownResponse>> getTownsByCity(@PathVariable UUID cityId) {
        return ApiResponse.success(addressService.getTownsByCity(cityId));
    }

    @PostMapping("/admin/towns")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TownResponse> createTown(@Valid @RequestBody CreateTownRequest request) {
        return ApiResponse.success(addressService.createTown(request), "Commune créée avec succès");
    }

    @PutMapping("/admin/towns/{id}")
    public ApiResponse<TownResponse> updateTown(@PathVariable UUID id, @Valid @RequestBody UpdateTownRequest request) {
        return ApiResponse.success(addressService.updateTown(id, request), "Commune modifiée avec succès");
    }

    // === Districts ===
    // Symfony: GET /districts?cityId={uuid}
    @GetMapping("/districts")
    public ApiResponse<List<DistrictResponse>> getDistricts(@RequestParam(required = false) UUID cityId) {
        if (cityId != null) {
            return ApiResponse.success(addressService.getDistrictsByCity(cityId));
        }
        return ApiResponse.success(addressService.getDistrictsByCity(null));
    }

    // Keep nested endpoints for backward compat
    @GetMapping("/towns/{id}/districts")
    public ApiResponse<List<DistrictResponse>> getDistrictsByTown(@PathVariable UUID id) {
        return ApiResponse.success(addressService.getDistrictsByTown(id));
    }

    @GetMapping("/cities/{id}/districts")
    public ApiResponse<List<DistrictResponse>> getDistrictsByCity(@PathVariable UUID id) {
        return ApiResponse.success(addressService.getDistrictsByCity(id));
    }

    @PostMapping("/admin/districts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DistrictResponse> createDistrict(@Valid @RequestBody CreateDistrictRequest request) {
        return ApiResponse.success(addressService.createDistrict(request), "Quartier créé avec succès");
    }

    @DeleteMapping("/admin/districts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDistrict(@PathVariable UUID id) {
        addressService.deleteDistrict(id);
    }

    // === Addresses ===
    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AddressResponse> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        return ApiResponse.success(addressService.createAddress(request), "Adresse créée avec succès");
    }

    @PutMapping("/addresses/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable UUID id, @Valid @RequestBody UpdateAddressRequest request) {
        return ApiResponse.success(addressService.updateAddress(id, request), "Adresse modifiée avec succès");
    }

    @GetMapping("/addresses/search")
    public ApiResponse<List<AddressResponse>> searchAddresses(@RequestParam String q) {
        return ApiResponse.success(addressService.searchAddresses(q));
    }

    // === Geography Search ===
    // Symfony: GET /geography/search?q=xxx
    @GetMapping("/geography/search")
    public ApiResponse<Object> geographySearch(@RequestParam String q) {
        return ApiResponse.success(addressService.searchAddresses(q));
    }
}
