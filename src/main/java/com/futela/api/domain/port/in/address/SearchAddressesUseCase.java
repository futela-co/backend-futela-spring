package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.AddressResponse;

import java.util.List;

public interface SearchAddressesUseCase {
    List<AddressResponse> execute(String query);
}
