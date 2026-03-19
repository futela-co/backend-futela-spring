package com.futela.api.infrastructure.persistence.adapter.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.rent.RentInvoiceEntity;
import com.futela.api.infrastructure.persistence.mapper.rent.RentInvoiceMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentInvoiceRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RentInvoiceRepositoryAdapter implements RentInvoiceRepositoryPort {

    private final JpaRentInvoiceRepository jpaRepository;
    private final JpaLeaseRepository leaseRepository;

    public RentInvoiceRepositoryAdapter(JpaRentInvoiceRepository jpaRepository, JpaLeaseRepository leaseRepository) {
        this.jpaRepository = jpaRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public RentInvoice save(RentInvoice invoice) {
        RentInvoiceEntity entity;
        if (invoice.id() != null) {
            entity = jpaRepository.findById(invoice.id()).orElse(new RentInvoiceEntity());
        } else {
            entity = new RentInvoiceEntity();
        }
        entity.setInvoiceNumber(invoice.invoiceNumber());
        entity.setAmount(invoice.amount());
        entity.setPaidAmount(invoice.paidAmount());
        entity.setStatus(invoice.status());
        entity.setDueDate(invoice.dueDate());
        entity.setPeriodStart(invoice.periodStart());
        entity.setPeriodEnd(invoice.periodEnd());
        entity.setLateFee(invoice.lateFee());
        if (entity.getLease() == null && invoice.leaseId() != null) {
            leaseRepository.findById(invoice.leaseId()).ifPresent(entity::setLease);
        }
        if (entity.getCompany() == null && entity.getLease() != null) {
            entity.setCompany(entity.getLease().getCompany());
        }
        return RentInvoiceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RentInvoice> findById(UUID id) {
        return jpaRepository.findById(id).map(RentInvoiceMapper::toDomain);
    }

    @Override
    public List<RentInvoice> findByLeaseId(UUID leaseId) {
        return jpaRepository.findByLeaseIdAndDeletedAtIsNull(leaseId).stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public List<RentInvoice> findUnpaidByLeaseId(UUID leaseId) {
        return jpaRepository.findUnpaidByLeaseId(leaseId).stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public List<RentInvoice> findByStatus(PaymentStatus status) {
        return jpaRepository.findByStatusAndDeletedAtIsNull(status).stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public List<RentInvoice> findOverdue() {
        return jpaRepository.findOverdue().stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public List<RentInvoice> findPendingBeforeDueDate(LocalDate date) {
        return jpaRepository.findPendingBeforeDueDate(date).stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public List<RentInvoice> findByLandlordId(UUID landlordId) {
        return jpaRepository.findByLandlordId(landlordId).stream()
                .map(RentInvoiceMapper::toDomain).toList();
    }

    @Override
    public Optional<RentInvoice> findByLeaseIdAndPeriod(UUID leaseId, LocalDate periodStart, LocalDate periodEnd) {
        return jpaRepository.findByLeaseIdAndPeriodStartAndPeriodEndAndDeletedAtIsNull(leaseId, periodStart, periodEnd)
                .map(RentInvoiceMapper::toDomain);
    }

    @Override
    public long countByLeaseIdAndStatus(UUID leaseId, PaymentStatus status) {
        return jpaRepository.countByLeaseIdAndStatusAndDeletedAtIsNull(leaseId, status);
    }

    @Override
    public long countByLandlordIdAndStatus(UUID landlordId, PaymentStatus status) {
        return jpaRepository.countByLandlordIdAndStatus(landlordId, status);
    }
}
