package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.port.in.rent.GetTenantRentInvoicesUseCase;
import com.futela.api.infrastructure.persistence.mapper.rent.RentInvoiceMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentInvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetTenantRentInvoicesService implements GetTenantRentInvoicesUseCase {

    private final JpaRentInvoiceRepository rentInvoiceRepository;

    public GetTenantRentInvoicesService(JpaRentInvoiceRepository rentInvoiceRepository) {
        this.rentInvoiceRepository = rentInvoiceRepository;
    }

    @Override
    public List<RentInvoiceResponse> execute(UUID tenantId) {
        return rentInvoiceRepository.findByTenantId(tenantId).stream()
                .map(RentInvoiceMapper::toDomain)
                .map(RentInvoiceResponse::from)
                .toList();
    }
}
