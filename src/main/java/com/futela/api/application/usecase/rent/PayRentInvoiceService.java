package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.PayRentInvoiceRequest;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentPaymentReceivedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.model.rent.RentPayment;
import com.futela.api.domain.port.in.rent.PayRentInvoiceUseCase;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PayRentInvoiceService implements PayRentInvoiceUseCase {

    private final RentInvoiceRepositoryPort invoiceRepository;
    private final RentPaymentRepositoryPort paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PayRentInvoiceService(RentInvoiceRepositoryPort invoiceRepository,
                                 RentPaymentRepositoryPort paymentRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RentPaymentResponse execute(UUID invoiceId, PayRentInvoiceRequest request) {
        RentInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("RentInvoice", invoiceId.toString()));

        if (!invoice.canBePaid()) {
            throw new InvalidOperationException("La facture ne peut pas être payée dans son statut actuel");
        }

        // Calculate late fee: 5% after 7 days overdue
        BigDecimal lateFee = BigDecimal.ZERO;
        if (request.paymentDate().isAfter(invoice.dueDate())) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(invoice.dueDate(), request.paymentDate());
            if (daysLate > 7) {
                lateFee = invoice.amount().multiply(new BigDecimal("0.05"));
            }
        }

        // Record payment
        RentPayment payment = new RentPayment(
                null, invoiceId, invoice.leaseId(),
                request.amount(), request.paymentDate(),
                request.paymentMethod(), request.reference(), request.notes(),
                invoice.companyId(), null
        );

        RentPayment saved = paymentRepository.save(payment);

        // Update invoice paid amount and status
        BigDecimal totalPaid = invoice.paidAmount().add(request.amount());
        PaymentStatus newStatus;
        if (totalPaid.compareTo(invoice.amount()) >= 0) {
            newStatus = PaymentStatus.PAID;
        } else {
            newStatus = PaymentStatus.PARTIAL;
        }

        RentInvoice updated = new RentInvoice(
                invoice.id(), invoice.leaseId(), invoice.invoiceNumber(),
                invoice.amount(), totalPaid, newStatus,
                invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                lateFee, invoice.companyId(),
                invoice.createdAt(), invoice.updatedAt()
        );
        invoiceRepository.save(updated);

        eventPublisher.publishEvent(new RentPaymentReceivedEvent(saved.id(), invoiceId, request.amount()));
        return RentPaymentResponse.from(saved);
    }
}
