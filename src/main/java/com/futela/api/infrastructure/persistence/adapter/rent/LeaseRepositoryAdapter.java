package com.futela.api.infrastructure.persistence.adapter.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.rent.LeaseEntity;
import com.futela.api.infrastructure.persistence.mapper.rent.LeaseMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LeaseRepositoryAdapter implements LeaseRepositoryPort {

    private final JpaLeaseRepository jpaRepository;

    public LeaseRepositoryAdapter(JpaLeaseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lease save(Lease lease) {
        LeaseEntity entity;
        if (lease.id() != null) {
            entity = jpaRepository.findById(lease.id()).orElse(new LeaseEntity());
        } else {
            entity = new LeaseEntity();
        }
        updateEntity(entity, lease);
        return LeaseMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Lease> findById(UUID id) {
        return jpaRepository.findByIdAndNotDeleted(id)
                .map(LeaseMapper::toDomain);
    }

    @Override
    public List<Lease> findByLandlordId(UUID landlordId) {
        return jpaRepository.findByLandlordIdAndDeletedAtIsNull(landlordId).stream()
                .map(LeaseMapper::toDomain)
                .toList();
    }

    @Override
    public List<Lease> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantIdAndDeletedAtIsNull(tenantId).stream()
                .map(LeaseMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Lease> findActiveByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyIdAndStatusAndDeletedAtIsNull(propertyId, LeaseStatus.ACTIVE)
                .map(LeaseMapper::toDomain);
    }

    @Override
    public List<Lease> findByStatus(LeaseStatus status) {
        return jpaRepository.findByStatusAndDeletedAtIsNull(status).stream()
                .map(LeaseMapper::toDomain)
                .toList();
    }

    @Override
    public List<Lease> findAll() {
        return jpaRepository.findByDeletedAtIsNull().stream()
                .map(LeaseMapper::toDomain)
                .toList();
    }

    @Override
    public long countByLandlordId(UUID landlordId) {
        return jpaRepository.countByLandlordIdAndDeletedAtIsNull(landlordId);
    }

    @Override
    public long countActiveByLandlordId(UUID landlordId) {
        return jpaRepository.countByLandlordIdAndStatusAndDeletedAtIsNull(landlordId, LeaseStatus.ACTIVE);
    }

    @Override
    public long countActive() {
        return jpaRepository.countByDeletedAtIsNull();
    }

    private void updateEntity(LeaseEntity entity, Lease lease) {
        entity.setStatus(lease.status());
        entity.setMonthlyRent(lease.monthlyRent());
        entity.setCurrency(lease.currency());
        entity.setDepositAmount(lease.depositAmount());
        entity.setStartDate(lease.startDate());
        entity.setEndDate(lease.endDate());
        entity.setPaymentDayOfMonth(lease.paymentDayOfMonth());
        entity.setNotes(lease.notes());
        entity.setTerminatedAt(lease.terminatedAt());
        entity.setTerminationReason(lease.terminationReason());
    }
}
