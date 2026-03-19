package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.port.in.rent.GetLandlordLeasesUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetLandlordLeasesService implements GetLandlordLeasesUseCase {
    private final LeaseRepositoryPort leaseRepository;

    public GetLandlordLeasesService(LeaseRepositoryPort leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<LeaseResponse> execute(UUID landlordId) {
        return leaseRepository.findByLandlordId(landlordId).stream()
                .map(LeaseResponse::from).toList();
    }
}
