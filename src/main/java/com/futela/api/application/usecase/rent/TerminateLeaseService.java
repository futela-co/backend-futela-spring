package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.TerminateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.event.rent.LeaseTerminatedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.in.rent.TerminateLeaseUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class TerminateLeaseService implements TerminateLeaseUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TerminateLeaseService(LeaseRepositoryPort leaseRepository, ApplicationEventPublisher eventPublisher) {
        this.leaseRepository = leaseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public LeaseResponse execute(UUID leaseId, TerminateLeaseRequest request) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId.toString()));

        if (!lease.isActive()) {
            throw new InvalidOperationException("Seuls les baux actifs peuvent être résiliés");
        }

        Lease terminated = new Lease(
                lease.id(), lease.propertyId(), lease.propertyTitle(),
                lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                LeaseStatus.TERMINATED,
                lease.monthlyRent(), lease.currency(), lease.depositAmount(),
                lease.startDate(), lease.endDate(), lease.paymentDayOfMonth(), lease.notes(),
                Instant.now(), request.reason(), lease.companyId(),
                lease.createdAt(), lease.updatedAt(), lease.deletedAt()
        );

        Lease saved = leaseRepository.save(terminated);
        eventPublisher.publishEvent(new LeaseTerminatedEvent(saved.id(), request.reason()));
        return LeaseResponse.from(saved);
    }
}
