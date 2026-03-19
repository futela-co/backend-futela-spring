package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.port.in.rent.GetRentPaymentsByLeaseUseCase;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetRentPaymentsByLeaseService implements GetRentPaymentsByLeaseUseCase {
    private final RentPaymentRepositoryPort paymentRepository;

    public GetRentPaymentsByLeaseService(RentPaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<RentPaymentResponse> execute(UUID leaseId) {
        return paymentRepository.findByLeaseId(leaseId).stream()
                .map(RentPaymentResponse::from).toList();
    }
}
