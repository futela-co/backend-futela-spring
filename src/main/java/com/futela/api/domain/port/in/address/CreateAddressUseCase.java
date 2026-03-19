package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateAddressRequest;
import com.futela.api.application.dto.response.address.AddressResponse;

public interface CreateAddressUseCase {
    AddressResponse execute(CreateAddressRequest request);
}
