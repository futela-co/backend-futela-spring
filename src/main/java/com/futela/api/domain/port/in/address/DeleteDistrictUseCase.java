package com.futela.api.domain.port.in.address;

import java.util.UUID;

public interface DeleteDistrictUseCase {
    void execute(UUID id);
}
