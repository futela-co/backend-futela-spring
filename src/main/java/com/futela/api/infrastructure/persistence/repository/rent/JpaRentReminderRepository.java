package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.domain.enums.ReminderType;
import com.futela.api.infrastructure.persistence.entity.rent.RentReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRentReminderRepository extends JpaRepository<RentReminderEntity, UUID> {
    List<RentReminderEntity> findByLeaseIdAndDeletedAtIsNull(UUID leaseId);
    List<RentReminderEntity> findByInvoiceIdAndDeletedAtIsNull(UUID invoiceId);
    boolean existsByInvoiceIdAndTypeAndDeletedAtIsNull(UUID invoiceId, ReminderType type);
}
