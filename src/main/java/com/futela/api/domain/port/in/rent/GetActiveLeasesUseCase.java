package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;

import java.util.List;

public interface GetActiveLeasesUseCase {
    List<LeaseResponse> execute();
}
