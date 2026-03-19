package com.futela.api.domain.port.out.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.model.rent.Lease;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseRepositoryPort {
    Lease save(Lease lease);
    Optional<Lease> findById(UUID id);
    List<Lease> findByLandlordId(UUID landlordId);
    List<Lease> findByTenantId(UUID tenantId);
    Optional<Lease> findActiveByPropertyId(UUID propertyId);
    List<Lease> findByStatus(LeaseStatus status);
    List<Lease> findAll();
    long countByLandlordId(UUID landlordId);
    long countActiveByLandlordId(UUID landlordId);
}
