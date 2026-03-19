package com.futela.api.domain.port.out.reservation;

import com.futela.api.domain.model.reservation.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepositoryPort {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(UUID id);

    List<Reservation> findByPropertyId(UUID propertyId);

    List<Reservation> findByUserId(UUID userId);

    boolean existsOverlapping(UUID propertyId, LocalDate startDate, LocalDate endDate, UUID excludeId);
}
