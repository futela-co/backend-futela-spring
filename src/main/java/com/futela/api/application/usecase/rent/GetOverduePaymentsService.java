package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.port.in.rent.GetOverduePaymentsUseCase;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetOverduePaymentsService implements GetOverduePaymentsUseCase {
    private final RentInvoiceRepositoryPort invoiceRepository;
    private final LeaseRepositoryPort leaseRepository;

    public GetOverduePaymentsService(RentInvoiceRepositoryPort invoiceRepository, LeaseRepositoryPort leaseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public List<RentInvoiceResponse> execute(UUID landlordId) {
        return leaseRepository.findByLandlordId(landlordId).stream()
                .flatMap(l -> invoiceRepository.findByLeaseId(l.id()).stream())
                .filter(i -> i.status() == PaymentStatus.OVERDUE)
                .map(RentInvoiceResponse::from)
                .toList();
    }
}
