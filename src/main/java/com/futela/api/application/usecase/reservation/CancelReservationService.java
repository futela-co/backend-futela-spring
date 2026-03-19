package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.CancelReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.event.ReservationCancelledEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.CancelReservationUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class CancelReservationService implements CancelReservationUseCase {

    private final JpaReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CancelReservationService(
            JpaReservationRepository reservationRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReservationResponse execute(UUID reservationId, CancelReservationRequest request) {
        ReservationEntity entity = reservationRepository.findByIdAndDeletedAtIsNull(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", reservationId.toString()));

        if (entity.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidOperationException("Cette réservation est déjà annulée");
        }

        if (entity.getStatus() == ReservationStatus.COMPLETED) {
            throw new InvalidOperationException("Impossible d'annuler une réservation terminée");
        }

        entity.setStatus(ReservationStatus.CANCELLED);
        entity.setCancelledAt(Instant.now());
        if (request != null && request.reason() != null) {
            entity.setCancelReason(request.reason());
        }

        ReservationEntity saved = reservationRepository.save(entity);

        eventPublisher.publishEvent(new ReservationCancelledEvent(
                saved.getId(), saved.getProperty().getId(), saved.getUser().getId(),
                request != null ? request.reason() : null
        ));

        return ReservationResponseMapper.fromEntity(saved);
    }
}
