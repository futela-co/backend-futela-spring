package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.CompleteReservationUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class CompleteReservationService implements CompleteReservationUseCase {

    private final JpaReservationRepository reservationRepository;

    public CompleteReservationService(JpaReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ReservationResponse execute(UUID reservationId) {
        ReservationEntity entity = reservationRepository.findByIdAndDeletedAtIsNull(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", reservationId.toString()));

        if (entity.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidOperationException("Seules les réservations confirmées peuvent être marquées comme terminées");
        }

        entity.setStatus(ReservationStatus.COMPLETED);
        entity.setCompletedAt(Instant.now());

        ReservationEntity saved = reservationRepository.save(entity);
        return ReservationResponseMapper.fromEntity(saved);
    }
}
