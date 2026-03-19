package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.port.in.rent.GetUnpaidInvoicesUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetUnpaidInvoicesService implements GetUnpaidInvoicesUseCase {
    private final RentInvoiceRepositoryPort invoiceRepository;
    private final LeaseRepositoryPort leaseRepository;

    public GetUnpaidInvoicesService(RentInvoiceRepositoryPort invoiceRepository, LeaseRepositoryPort leaseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<RentInvoiceResponse> execute(UUID landlordId) {
        return leaseRepository.findByLandlordId(landlordId).stream()
                .flatMap(lease -> invoiceRepository.findUnpaidByLeaseId(lease.id()).stream())
                .map(RentInvoiceResponse::from)
                .toList();
    }
}
