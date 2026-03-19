package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.port.in.rent.GetTenantRentPaymentsUseCase;
import com.futela.api.infrastructure.persistence.mapper.rent.RentPaymentMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetTenantRentPaymentsService implements GetTenantRentPaymentsUseCase {

    private final JpaRentPaymentRepository rentPaymentRepository;

    public GetTenantRentPaymentsService(JpaRentPaymentRepository rentPaymentRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
    }

    @Override
    public List<RentPaymentResponse> execute(UUID tenantId) {
        return rentPaymentRepository.findByTenantId(tenantId).stream()
                .map(RentPaymentMapper::toDomain)
                .map(RentPaymentResponse::from)
                .toList();
    }
}
