package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.UpdateAddressRequest;
import com.futela.api.application.dto.response.address.AddressResponse;

import java.util.UUID;

public interface UpdateAddressUseCase {
    AddressResponse execute(UUID id, UpdateAddressRequest request);
}
