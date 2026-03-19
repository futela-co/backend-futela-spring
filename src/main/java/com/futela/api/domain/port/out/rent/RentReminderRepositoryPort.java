package com.futela.api.domain.port.out.rent;

import com.futela.api.domain.enums.ReminderType;
import com.futela.api.domain.model.rent.RentReminder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentReminderRepositoryPort {
    RentReminder save(RentReminder reminder);
    Optional<RentReminder> findById(UUID id);
    List<RentReminder> findByLeaseId(UUID leaseId);
    List<RentReminder> findByInvoiceId(UUID invoiceId);
    boolean existsByInvoiceIdAndType(UUID invoiceId, ReminderType type);
}
