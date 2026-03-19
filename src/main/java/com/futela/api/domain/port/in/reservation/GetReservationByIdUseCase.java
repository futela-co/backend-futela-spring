package com.futela.api.domain.port.in.reservation;

import com.futela.api.application.dto.response.reservation.ReservationResponse;

import java.util.UUID;

public interface GetReservationByIdUseCase {
    ReservationResponse execute(UUID reservationId);
}
