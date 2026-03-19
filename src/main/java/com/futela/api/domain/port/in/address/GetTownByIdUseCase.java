package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.TownResponse;

import java.util.UUID;

public interface GetTownByIdUseCase {
    TownResponse execute(UUID id);
}
