package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentInvoiceGeneratedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.port.in.rent.GenerateRentInvoiceUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class GenerateRentInvoiceService implements GenerateRentInvoiceUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final RentInvoiceRepositoryPort invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GenerateRentInvoiceService(LeaseRepositoryPort leaseRepository,
                                      RentInvoiceRepositoryPort invoiceRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RentInvoiceResponse execute(UUID leaseId, int month, int year) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId.toString()));

        if (!lease.isActive()) {
            throw new InvalidOperationException("Impossible de générer une facture pour un bail inactif");
        }

        LocalDate periodStart = LocalDate.of(year, month, 1);
        LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

        // Check duplicate
        invoiceRepository.findByLeaseIdAndPeriod(leaseId, periodStart, periodEnd).ifPresent(existing -> {
            throw new InvalidOperationException("Une facture existe déjà pour cette période");
        });

        int day = Math.min(lease.paymentDayOfMonth(), periodStart.lengthOfMonth());
        LocalDate dueDate = LocalDate.of(year, month, day);

        String invoiceNumber = generateInvoiceNumber(year, month);

        RentInvoice invoice = new RentInvoice(
                null, leaseId, invoiceNumber,
                lease.monthlyRent(), BigDecimal.ZERO,
                PaymentStatus.PENDING,
                dueDate, periodStart, periodEnd,
                BigDecimal.ZERO, lease.companyId(),
                null, null
        );

        RentInvoice saved = invoiceRepository.save(invoice);
        eventPublisher.publishEvent(new RentInvoiceGeneratedEvent(saved.id(), leaseId, saved.amount(), dueDate));
        return RentInvoiceResponse.from(saved);
    }

    private String generateInvoiceNumber(int year, int month) {
        String unique = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("FUT-%d-%02d%02d-%s", year, month, LocalDate.now().getDayOfMonth(), unique);
    }
}
