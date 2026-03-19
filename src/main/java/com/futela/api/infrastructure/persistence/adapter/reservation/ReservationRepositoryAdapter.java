package com.futela.api.infrastructure.persistence.adapter.reservation;

import com.futela.api.domain.model.reservation.Reservation;
import com.futela.api.domain.port.out.reservation.ReservationRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.mapper.reservation.ReservationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReservationRepositoryAdapter implements ReservationRepositoryPort {

    private final JpaReservationRepository jpaRepository;

    public ReservationRepositoryAdapter(JpaReservationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity;

        if (reservation.id() != null) {
            entity = jpaRepository.findById(reservation.id()).orElse(new ReservationEntity());
        } else {
            entity = new ReservationEntity();
        }

        ReservationPersistenceMapper.updateEntity(entity, reservation);
        ReservationEntity saved = jpaRepository.save(entity);
        return ReservationPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(ReservationPersistenceMapper::toDomain);
    }

    @Override
    public List<Reservation> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyIdAndDeletedAtIsNull(propertyId).stream()
                .map(ReservationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(ReservationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsOverlapping(UUID propertyId, LocalDate startDate, LocalDate endDate, UUID excludeId) {
        return jpaRepository.existsOverlapping(propertyId, startDate, endDate, excludeId);
    }
}
