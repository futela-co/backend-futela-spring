package com.futela.api.infrastructure.persistence.adapter.rent;

import com.futela.api.domain.model.rent.RentPayment;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.rent.RentPaymentEntity;
import com.futela.api.infrastructure.persistence.mapper.rent.RentPaymentMapper;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentPaymentRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaLeaseRepository;
import com.futela.api.infrastructure.persistence.repository.rent.JpaRentInvoiceRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RentPaymentRepositoryAdapter implements RentPaymentRepositoryPort {

    private final JpaRentPaymentRepository jpaRepository;
    private final JpaLeaseRepository leaseRepository;
    private final JpaRentInvoiceRepository invoiceRepository;

    public RentPaymentRepositoryAdapter(JpaRentPaymentRepository jpaRepository,
                                        JpaLeaseRepository leaseRepository,
                                        JpaRentInvoiceRepository invoiceRepository) {
        this.jpaRepository = jpaRepository;
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public RentPayment save(RentPayment payment) {
        RentPaymentEntity entity = new RentPaymentEntity();
        entity.setAmount(payment.amount());
        entity.setPaymentDate(payment.paymentDate());
        entity.setPaymentMethod(payment.paymentMethod());
        entity.setReference(payment.reference());
        entity.setNotes(payment.notes());
        if (payment.leaseId() != null) {
            leaseRepository.findById(payment.leaseId()).ifPresent(lease -> {
                entity.setLease(lease);
                entity.setCompany(lease.getCompany());
            });
        }
        if (payment.invoiceId() != null) {
            invoiceRepository.findById(payment.invoiceId()).ifPresent(entity::setInvoice);
        }
        return RentPaymentMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RentPayment> findById(UUID id) {
        return jpaRepository.findById(id).map(RentPaymentMapper::toDomain);
    }

    @Override
    public List<RentPayment> findByLeaseId(UUID leaseId) {
        return jpaRepository.findByLeaseIdAndDeletedAtIsNull(leaseId).stream()
                .map(RentPaymentMapper::toDomain).toList();
    }

    @Override
    public List<RentPayment> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceIdAndDeletedAtIsNull(invoiceId).stream()
                .map(RentPaymentMapper::toDomain).toList();
    }

    @Override
    public BigDecimal sumByInvoiceId(UUID invoiceId) {
        return jpaRepository.sumByInvoiceId(invoiceId);
    }

    @Override
    public BigDecimal sumByLandlordIdAndYear(UUID landlordId, int year) {
        return jpaRepository.sumByLandlordIdAndYear(landlordId, year);
    }
}
