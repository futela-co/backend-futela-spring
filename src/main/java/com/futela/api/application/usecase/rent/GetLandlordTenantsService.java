package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.TenantSummaryResponse;
import com.futela.api.domain.port.in.rent.GetLandlordTenantsUseCase;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetLandlordTenantsService implements GetLandlordTenantsUseCase {

    private final JpaLeaseRepository leaseRepository;

    public GetLandlordTenantsService(JpaLeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<TenantSummaryResponse> execute(UUID landlordId) {
        return leaseRepository.findByLandlordIdAndDeletedAtIsNull(landlordId).stream()
                .map(lease -> lease.getTenant())
                .collect(Collectors.toMap(UserEntity::getId, tenant -> tenant, (a, b) -> a))
                .values()
                .stream()
                .map(tenant -> new TenantSummaryResponse(
                        tenant.getId(),
                        tenant.getFirstName(),
                        tenant.getLastName(),
                        tenant.getEmail(),
                        tenant.getPhone(),
                        tenant.getAvatar()
                ))
                .toList();
    }
}
