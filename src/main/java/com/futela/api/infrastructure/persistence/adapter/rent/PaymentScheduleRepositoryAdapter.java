package com.futela.api.infrastructure.persistence.adapter.rent;

import com.futela.api.domain.model.rent.PaymentSchedule;
import com.futela.api.domain.port.out.rent.PaymentScheduleRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.rent.PaymentScheduleEntity;
import com.futela.api.infrastructure.persistence.mapper.rent.PaymentScheduleMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaPaymentScheduleRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentScheduleRepositoryAdapter implements PaymentScheduleRepositoryPort {

    private final JpaPaymentScheduleRepository jpaRepository;
    private final JpaLeaseRepository leaseRepository;

    public PaymentScheduleRepositoryAdapter(JpaPaymentScheduleRepository jpaRepository, JpaLeaseRepository leaseRepository) {
        this.jpaRepository = jpaRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public PaymentSchedule save(PaymentSchedule schedule) {
        PaymentScheduleEntity entity = new PaymentScheduleEntity();
        entity.setDueDate(schedule.dueDate());
        entity.setAmount(schedule.amount());
        entity.setStatus(schedule.status());
        entity.setInvoiceId(schedule.invoiceId());
        if (schedule.leaseId() != null) {
            leaseRepository.findById(schedule.leaseId()).ifPresent(lease -> {
                entity.setLease(lease);
                entity.setCompany(lease.getCompany());
            });
        }
        return PaymentScheduleMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PaymentSchedule> findById(UUID id) {
        return jpaRepository.findById(id).map(PaymentScheduleMapper::toDomain);
    }

    @Override
    public List<PaymentSchedule> findByLeaseId(UUID leaseId) {
        return jpaRepository.findByLeaseIdAndDeletedAtIsNull(leaseId).stream()
                .map(PaymentScheduleMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByLeaseId(UUID leaseId) {
        jpaRepository.deleteByLeaseId(leaseId);
    }

    @Override
    public List<PaymentSchedule> saveAll(List<PaymentSchedule> schedules) {
        return schedules.stream().map(this::save).toList();
    }
}
