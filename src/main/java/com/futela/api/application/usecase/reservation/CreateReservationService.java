package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.CreateReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.event.ReservationCreatedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.port.in.reservation.CreateReservationUseCase;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class CreateReservationService implements CreateReservationUseCase {

    private final JpaReservationRepository reservationRepository;
    private final jakarta.persistence.EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    public CreateReservationService(
            JpaReservationRepository reservationRepository,
            jakarta.persistence.EntityManager entityManager,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reservationRepository = reservationRepository;
        this.entityManager = entityManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReservationResponse execute(CreateReservationRequest request, UUID currentUserId) {
        // Validate dates
        if (request.endDate().isBefore(request.startDate()) || request.endDate().isEqual(request.startDate())) {
            throw new ValidationException("La date de fin doit être après la date de début");
        }

        // Load property
        PropertyEntity property = entityManager.find(PropertyEntity.class, request.propertyId());
        if (property == null) {
            throw new ResourceNotFoundException("Propriété", request.propertyId().toString());
        }

        // Load user (guest)
        UserEntity guest = entityManager.find(UserEntity.class, currentUserId);
        if (guest == null) {
            throw new ResourceNotFoundException("Utilisateur", currentUserId.toString());
        }

        // Load host (property owner)
        UserEntity host = property.getOwner();

        // Check availability (no overlapping reservations)
        boolean hasOverlap = reservationRepository.existsOverlapping(
                request.propertyId(), request.startDate(), request.endDate(), null
        );
        if (hasOverlap) {
            throw new InvalidOperationException("La propriété n'est pas disponible pour les dates sélectionnées");
        }

        // Calculate price: (endDate - startDate).days * property.pricePerDay
        long numberOfDays = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        BigDecimal pricePerDay = property.getPricePerDay();
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(numberOfDays));

        // Create reservation entity
        ReservationEntity entity = new ReservationEntity();
        entity.setProperty(property);
        entity.setUser(guest);
        entity.setHost(host);
        entity.setCompany(property.getCompany());
        entity.setStatus(ReservationStatus.PENDING);
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setTotalPrice(totalPrice);
        entity.setCurrency("USD");
        entity.setGuestCount(request.guestCount());
        entity.setNotes(request.notes());

        ReservationEntity saved = reservationRepository.save(entity);

        // Publish domain event
        eventPublisher.publishEvent(new ReservationCreatedEvent(
                saved.getId(), property.getId(), guest.getId(), host.getId()
        ));

        return ReservationResponseMapper.fromEntity(saved);
    }
}
