package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.RecordRentPaymentRequest;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.model.rent.RentPayment;
import com.futela.api.domain.port.in.rent.RecordRentPaymentUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RecordRentPaymentService implements RecordRentPaymentUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final RentPaymentRepositoryPort paymentRepository;

    public RecordRentPaymentService(LeaseRepositoryPort leaseRepository, RentPaymentRepositoryPort paymentRepository) {
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public RentPaymentResponse execute(UUID leaseId, RecordRentPaymentRequest request) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId.toString()));

        RentPayment payment = new RentPayment(
                null, request.invoiceId(), leaseId,
                request.amount(), request.paymentDate(),
                request.paymentMethod(), request.reference(), request.notes(),
                lease.companyId(), null
        );

        return RentPaymentResponse.from(paymentRepository.save(payment));
    }
}
