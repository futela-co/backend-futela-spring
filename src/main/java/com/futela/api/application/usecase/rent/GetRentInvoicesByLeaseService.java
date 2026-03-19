package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.port.in.rent.GetRentInvoicesByLeaseUseCase;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetRentInvoicesByLeaseService implements GetRentInvoicesByLeaseUseCase {
    private final RentInvoiceRepositoryPort invoiceRepository;

    public GetRentInvoicesByLeaseService(RentInvoiceRepositoryPort invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<RentInvoiceResponse> execute(UUID leaseId) {
        return invoiceRepository.findByLeaseId(leaseId).stream()
                .map(RentInvoiceResponse::from).toList();
    }
}
