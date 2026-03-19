package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.UpdateTownRequest;
import com.futela.api.application.dto.response.address.TownResponse;

import java.util.UUID;

public interface UpdateTownUseCase {
    TownResponse execute(UUID id, UpdateTownRequest request);
}
