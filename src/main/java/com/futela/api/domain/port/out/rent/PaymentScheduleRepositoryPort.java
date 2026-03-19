package com.futela.api.domain.port.out.rent;

import com.futela.api.domain.model.rent.PaymentSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentScheduleRepositoryPort {
    PaymentSchedule save(PaymentSchedule schedule);
    Optional<PaymentSchedule> findById(UUID id);
    List<PaymentSchedule> findByLeaseId(UUID leaseId);
    void deleteByLeaseId(UUID leaseId);
    List<PaymentSchedule> saveAll(List<PaymentSchedule> schedules);
}
