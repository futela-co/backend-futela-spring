package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.TownResponse;

import java.util.List;
import java.util.UUID;

public interface GetTownsByCityUseCase {
    List<TownResponse> execute(UUID cityId);
}
