package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.CreateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.model.rent.PaymentSchedule;
import com.futela.api.domain.port.in.rent.CreateLeaseUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.PaymentScheduleRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import com.futela.api.domain.event.rent.LeaseCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CreateLeaseService implements CreateLeaseUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final PaymentScheduleRepositoryPort scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateLeaseService(LeaseRepositoryPort leaseRepository,
                              PaymentScheduleRepositoryPort scheduleRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.leaseRepository = leaseRepository;
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public LeaseResponse execute(CreateLeaseRequest request) {
        // Validate dates
        if (!request.endDate().isAfter(request.startDate())) {
            throw new ValidationException("La date de fin doit être après la date de début");
        }

        // Check no active lease on property
        leaseRepository.findActiveByPropertyId(request.propertyId()).ifPresent(existing -> {
            throw new InvalidOperationException("La propriété a déjà un bail actif");
        });

        // Create lease
        Lease lease = new Lease(
                null,
                request.propertyId(),
                null,
                request.tenantId(),
                null,
                request.landlordId(),
                null,
                LeaseStatus.ACTIVE,
                request.monthlyRent(),
                request.currency(),
                request.depositAmount(),
                request.startDate(),
                request.endDate(),
                request.paymentDayOfMonth(),
                request.notes(),
                null,
                null,
                null,
                null,
                null,
                null
        );

        Lease saved = leaseRepository.save(lease);

        // Generate payment schedule
        List<PaymentSchedule> schedules = generateSchedule(saved);
        scheduleRepository.saveAll(schedules);

        // Publish event
        eventPublisher.publishEvent(new LeaseCreatedEvent(
                saved.id(), saved.propertyId(), saved.tenantId(), saved.landlordId()
        ));

        return LeaseResponse.from(saved);
    }

    private List<PaymentSchedule> generateSchedule(Lease lease) {
        List<PaymentSchedule> schedules = new ArrayList<>();
        LocalDate current = lease.startDate().withDayOfMonth(1);
        LocalDate end = lease.endDate();

        while (!current.isAfter(end)) {
            int day = Math.min(lease.paymentDayOfMonth(), current.lengthOfMonth());
            LocalDate dueDate = current.withDayOfMonth(day);
            if (!dueDate.isBefore(lease.startDate())) {
                schedules.add(new PaymentSchedule(
                        null,
                        lease.id(),
                        dueDate,
                        lease.monthlyRent(),
                        PaymentStatus.PENDING,
                        null,
                        lease.companyId(),
                        null
                ));
            }
            current = current.plusMonths(1);
        }
        return schedules;
    }
}
