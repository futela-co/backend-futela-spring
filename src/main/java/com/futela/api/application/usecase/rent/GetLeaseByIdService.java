package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.rent.GetLeaseByIdUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetLeaseByIdService implements GetLeaseByIdUseCase {

    private final LeaseRepositoryPort leaseRepository;

    public GetLeaseByIdService(LeaseRepositoryPort leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public LeaseResponse execute(UUID leaseId) {
        return leaseRepository.findById(leaseId)
                .map(LeaseResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId.toString()));
    }
}
