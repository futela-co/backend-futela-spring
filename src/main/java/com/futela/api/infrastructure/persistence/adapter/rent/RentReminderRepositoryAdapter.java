package com.futela.api.infrastructure.persistence.adapter.rent;

import com.futela.api.domain.enums.ReminderType;
import com.futela.api.domain.model.rent.RentReminder;
import com.futela.api.domain.port.out.rent.RentReminderRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.rent.RentReminderEntity;
import com.futela.api.infrastructure.persistence.mapper.rent.RentReminderMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentReminderRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentInvoiceRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RentReminderRepositoryAdapter implements RentReminderRepositoryPort {

    private final JpaRentReminderRepository jpaRepository;
    private final JpaLeaseRepository leaseRepository;
    private final JpaRentInvoiceRepository invoiceRepository;

    public RentReminderRepositoryAdapter(JpaRentReminderRepository jpaRepository,
                                         JpaLeaseRepository leaseRepository,
                                         JpaRentInvoiceRepository invoiceRepository) {
        this.jpaRepository = jpaRepository;
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public RentReminder save(RentReminder reminder) {
        RentReminderEntity entity = new RentReminderEntity();
        entity.setType(reminder.type());
        entity.setSentAt(reminder.sentAt() != null ? reminder.sentAt() : Instant.now());
        entity.setChannel(reminder.channel());
        if (reminder.leaseId() != null) {
            leaseRepository.findById(reminder.leaseId()).ifPresent(lease -> {
                entity.setLease(lease);
                entity.setCompany(lease.getCompany());
            });
        }
        if (reminder.invoiceId() != null) {
            invoiceRepository.findById(reminder.invoiceId()).ifPresent(entity::setInvoice);
        }
        return RentReminderMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RentReminder> findById(UUID id) {
        return jpaRepository.findById(id).map(RentReminderMapper::toDomain);
    }

    @Override
    public List<RentReminder> findByLeaseId(UUID leaseId) {
        return jpaRepository.findByLeaseIdAndDeletedAtIsNull(leaseId).stream()
                .map(RentReminderMapper::toDomain).toList();
    }

    @Override
    public List<RentReminder> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceIdAndDeletedAtIsNull(invoiceId).stream()
                .map(RentReminderMapper::toDomain).toList();
    }

    @Override
    public boolean existsByInvoiceIdAndType(UUID invoiceId, ReminderType type) {
        return jpaRepository.existsByInvoiceIdAndTypeAndDeletedAtIsNull(invoiceId, type);
    }
}
