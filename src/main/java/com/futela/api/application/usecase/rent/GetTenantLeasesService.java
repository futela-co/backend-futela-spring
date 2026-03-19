package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.port.in.rent.GetTenantLeasesUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetTenantLeasesService implements GetTenantLeasesUseCase {
    private final LeaseRepositoryPort leaseRepository;

    public GetTenantLeasesService(LeaseRepositoryPort leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<LeaseResponse> execute(UUID tenantId) {
        return leaseRepository.findByTenantId(tenantId).stream()
                .map(LeaseResponse::from).toList();
    }
}
