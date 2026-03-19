package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.GetReservationByIdUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetReservationByIdService implements GetReservationByIdUseCase {

    private final JpaReservationRepository reservationRepository;

    public GetReservationByIdService(JpaReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ReservationResponse execute(UUID reservationId) {
        ReservationEntity entity = reservationRepository.findByIdAndDeletedAtIsNull(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", reservationId.toString()));

        return ReservationResponseMapper.fromEntity(entity);
    }
}
