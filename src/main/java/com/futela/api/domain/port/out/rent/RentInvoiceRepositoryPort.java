package com.futela.api.domain.port.out.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.model.rent.RentInvoice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentInvoiceRepositoryPort {
    RentInvoice save(RentInvoice invoice);
    Optional<RentInvoice> findById(UUID id);
    List<RentInvoice> findByLeaseId(UUID leaseId);
    List<RentInvoice> findUnpaidByLeaseId(UUID leaseId);
    List<RentInvoice> findByStatus(PaymentStatus status);
    List<RentInvoice> findOverdue();
    List<RentInvoice> findPendingBeforeDueDate(LocalDate date);
    List<RentInvoice> findByLandlordId(UUID landlordId);
    Optional<RentInvoice> findByLeaseIdAndPeriod(UUID leaseId, LocalDate periodStart, LocalDate periodEnd);
    long countByLeaseIdAndStatus(UUID leaseId, PaymentStatus status);
    long countByLandlordIdAndStatus(UUID landlordId, PaymentStatus status);
}
