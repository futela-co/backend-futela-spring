package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.infrastructure.persistence.entity.rent.PaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPaymentScheduleRepository extends JpaRepository<PaymentScheduleEntity, UUID> {
    List<PaymentScheduleEntity> findByLeaseIdAndDeletedAtIsNull(UUID leaseId);
    void deleteByLeaseId(UUID leaseId);
}
