package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.port.in.rent.GetActiveLeasesUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetActiveLeasesService implements GetActiveLeasesUseCase {
    private final LeaseRepositoryPort leaseRepository;

    public GetActiveLeasesService(LeaseRepositoryPort leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<LeaseResponse> execute() {
        return leaseRepository.findByStatus(LeaseStatus.ACTIVE).stream()
                .map(LeaseResponse::from).toList();
    }
}
