package com.futela.api.infrastructure.persistence.adapter.reservation;

import com.futela.api.domain.model.reservation.Visit;
import com.futela.api.domain.port.out.reservation.VisitRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.mapper.reservation.VisitPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VisitRepositoryAdapter implements VisitRepositoryPort {

    private final JpaVisitRepository jpaRepository;

    public VisitRepositoryAdapter(JpaVisitRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Visit save(Visit visit) {
        VisitEntity entity;

        if (visit.id() != null) {
            entity = jpaRepository.findById(visit.id()).orElse(new VisitEntity());
        } else {
            entity = new VisitEntity();
        }

        VisitPersistenceMapper.updateEntity(entity, visit);
        VisitEntity saved = jpaRepository.save(entity);
        return VisitPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Visit> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(VisitPersistenceMapper::toDomain);
    }

    @Override
    public List<Visit> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyIdAndDeletedAtIsNull(propertyId).stream()
                .map(VisitPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Visit> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(VisitPersistenceMapper::toDomain)
                .toList();
    }
}
