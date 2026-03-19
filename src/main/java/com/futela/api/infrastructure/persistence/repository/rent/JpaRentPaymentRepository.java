package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.infrastructure.persistence.entity.rent.RentPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRentPaymentRepository extends JpaRepository<RentPaymentEntity, UUID> {
    List<RentPaymentEntity> findByLeaseIdAndDeletedAtIsNull(UUID leaseId);
    List<RentPaymentEntity> findByInvoiceIdAndDeletedAtIsNull(UUID invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM RentPaymentEntity p WHERE p.invoice.id = :invoiceId AND p.deletedAt IS NULL")
    BigDecimal sumByInvoiceId(UUID invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM RentPaymentEntity p WHERE p.lease.landlord.id = :landlordId AND YEAR(p.paymentDate) = :year AND p.deletedAt IS NULL")
    BigDecimal sumByLandlordIdAndYear(UUID landlordId, int year);
}
