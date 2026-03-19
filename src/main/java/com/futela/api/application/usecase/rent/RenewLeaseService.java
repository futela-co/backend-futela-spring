package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.RenewLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.event.rent.LeaseRenewedEvent;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.in.rent.RenewLeaseUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RenewLeaseService implements RenewLeaseUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RenewLeaseService(LeaseRepositoryPort leaseRepository, ApplicationEventPublisher eventPublisher) {
        this.leaseRepository = leaseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public LeaseResponse execute(UUID leaseId, RenewLeaseRequest request) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId.toString()));

        if (!lease.canBeRenewed()) {
            throw new InvalidOperationException("Seuls les baux actifs ou expirés peuvent être renouvelés");
        }

        if (!request.newEndDate().isAfter(lease.endDate())) {
            throw new ValidationException("La nouvelle date de fin doit être après la date de fin actuelle");
        }

        Lease renewed = new Lease(
                lease.id(), lease.propertyId(), lease.propertyTitle(),
                lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                LeaseStatus.ACTIVE,
                request.newMonthlyRent() != null ? request.newMonthlyRent() : lease.monthlyRent(),
                lease.currency(), lease.depositAmount(),
                lease.startDate(), request.newEndDate(),
                lease.paymentDayOfMonth(), lease.notes(),
                null, null, lease.companyId(),
                lease.createdAt(), lease.updatedAt(), lease.deletedAt()
        );

        Lease saved = leaseRepository.save(renewed);
        eventPublisher.publishEvent(new LeaseRenewedEvent(saved.id(), request.newEndDate()));
        return LeaseResponse.from(saved);
    }
}
