package com.futela.api.domain.port.out.rent;

import com.futela.api.domain.model.rent.RentPayment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentPaymentRepositoryPort {
    RentPayment save(RentPayment payment);
    Optional<RentPayment> findById(UUID id);
    List<RentPayment> findByLeaseId(UUID leaseId);
    List<RentPayment> findByInvoiceId(UUID invoiceId);
    BigDecimal sumByInvoiceId(UUID invoiceId);
    BigDecimal sumByLandlordIdAndYear(UUID landlordId, int year);
}
