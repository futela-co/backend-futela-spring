package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.infrastructure.persistence.entity.rent.RentInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRentInvoiceRepository extends JpaRepository<RentInvoiceEntity, UUID> {
    List<RentInvoiceEntity> findByLeaseIdAndDeletedAtIsNull(UUID leaseId);

    @Query("SELECT i FROM RentInvoiceEntity i WHERE i.lease.id = :leaseId AND i.status IN ('PENDING','OVERDUE','PARTIAL') AND i.deletedAt IS NULL")
    List<RentInvoiceEntity> findUnpaidByLeaseId(UUID leaseId);

    List<RentInvoiceEntity> findByStatusAndDeletedAtIsNull(PaymentStatus status);

    @Query("SELECT i FROM RentInvoiceEntity i WHERE i.status IN ('PENDING','PARTIAL') AND i.dueDate < CURRENT_DATE AND i.deletedAt IS NULL")
    List<RentInvoiceEntity> findOverdue();

    @Query("SELECT i FROM RentInvoiceEntity i WHERE i.status = 'PENDING' AND i.dueDate <= :date AND i.deletedAt IS NULL")
    List<RentInvoiceEntity> findPendingBeforeDueDate(LocalDate date);

    @Query("SELECT i FROM RentInvoiceEntity i WHERE i.lease.landlord.id = :landlordId AND i.deletedAt IS NULL")
    List<RentInvoiceEntity> findByLandlordId(UUID landlordId);

    Optional<RentInvoiceEntity> findByLeaseIdAndPeriodStartAndPeriodEndAndDeletedAtIsNull(UUID leaseId, LocalDate periodStart, LocalDate periodEnd);

    long countByLeaseIdAndStatusAndDeletedAtIsNull(UUID leaseId, PaymentStatus status);

    @Query("SELECT COUNT(i) FROM RentInvoiceEntity i WHERE i.lease.landlord.id = :landlordId AND i.status = :status AND i.deletedAt IS NULL")
    long countByLandlordIdAndStatus(UUID landlordId, PaymentStatus status);
}
