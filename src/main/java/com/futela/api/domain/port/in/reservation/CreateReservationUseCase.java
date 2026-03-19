package com.futela.api.domain.port.in.reservation;

import com.futela.api.application.dto.request.reservation.CreateReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;

public interface CreateReservationUseCase {
    ReservationResponse execute(CreateReservationRequest request, java.util.UUID currentUserId);
}
