package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.port.in.rent.GetPaymentHistoryUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetPaymentHistoryService implements GetPaymentHistoryUseCase {
    private final LeaseRepositoryPort leaseRepository;
    private final RentPaymentRepositoryPort paymentRepository;

    public GetPaymentHistoryService(LeaseRepositoryPort leaseRepository, RentPaymentRepositoryPort paymentRepository) {
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<RentPaymentResponse> execute(UUID landlordId) {
        return leaseRepository.findByLandlordId(landlordId).stream()
                .flatMap(l -> paymentRepository.findByLeaseId(l.id()).stream())
                .map(RentPaymentResponse::from)
                .toList();
    }
}
