package com.futela.api.domain.port.in.reservation;

import com.futela.api.application.dto.request.reservation.CancelReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;

import java.util.UUID;

public interface CancelReservationUseCase {
    ReservationResponse execute(UUID reservationId, CancelReservationRequest request);
}
