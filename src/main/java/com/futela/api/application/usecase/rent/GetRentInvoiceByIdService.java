package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.rent.GetRentInvoiceByIdUseCase;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetRentInvoiceByIdService implements GetRentInvoiceByIdUseCase {
    private final RentInvoiceRepositoryPort invoiceRepository;

    public GetRentInvoiceByIdService(RentInvoiceRepositoryPort invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public RentInvoiceResponse execute(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(RentInvoiceResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("RentInvoice", invoiceId.toString()));
    }
}
