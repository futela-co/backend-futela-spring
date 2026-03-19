package com.futela.api.domain.port.out.reservation;

import com.futela.api.domain.model.reservation.Visit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepositoryPort {

    Visit save(Visit visit);

    Optional<Visit> findById(UUID id);

    List<Visit> findByPropertyId(UUID propertyId);

    List<Visit> findByUserId(UUID userId);
}
