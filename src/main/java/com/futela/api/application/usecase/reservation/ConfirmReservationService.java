package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.event.ReservationConfirmedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.ConfirmReservationUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ConfirmReservationService implements ConfirmReservationUseCase {

    private final JpaReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConfirmReservationService(
            JpaReservationRepository reservationRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReservationResponse execute(UUID reservationId) {
        ReservationEntity entity = reservationRepository.findByIdAndDeletedAtIsNull(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", reservationId.toString()));

        if (entity.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Seules les réservations en attente peuvent être confirmées");
        }

        entity.setStatus(ReservationStatus.CONFIRMED);
        entity.setConfirmedAt(Instant.now());

        ReservationEntity saved = reservationRepository.save(entity);

        eventPublisher.publishEvent(new ReservationConfirmedEvent(
                saved.getId(), saved.getProperty().getId(), saved.getUser().getId()
        ));

        return ReservationResponseMapper.fromEntity(saved);
    }
}
