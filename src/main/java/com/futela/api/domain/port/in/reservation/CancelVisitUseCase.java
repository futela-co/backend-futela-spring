package com.futela.api.domain.port.in.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;

import java.util.UUID;

public interface CancelVisitUseCase {
    VisitResponse execute(UUID visitId);
}
