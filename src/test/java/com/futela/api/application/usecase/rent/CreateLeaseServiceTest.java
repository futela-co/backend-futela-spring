package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.CreateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.event.rent.LeaseCreatedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.model.rent.PaymentSchedule;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.PaymentScheduleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLeaseServiceTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private PaymentScheduleRepositoryPort scheduleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateLeaseService createLeaseService;

    private UUID propertyId;
    private UUID tenantId;
    private UUID landlordId;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        landlordId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Doit créer un bail avec succès quand aucun bail actif n'existe")
    void shouldCreateLeaseSuccessfully() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31),
                5, "Notes test"
        );

        when(leaseRepository.findActiveByPropertyId(propertyId)).thenReturn(Optional.empty());
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> {
            Lease lease = invocation.getArgument(0);
            return new Lease(
                    UUID.randomUUID(), lease.propertyId(), lease.propertyTitle(),
                    lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                    lease.status(), lease.monthlyRent(), lease.currency(), lease.depositAmount(),
                    lease.startDate(), lease.endDate(), lease.paymentDayOfMonth(), lease.notes(),
                    lease.terminatedAt(), lease.terminationReason(), lease.companyId(),
                    Instant.now(), Instant.now(), null
            );
        });
        when(scheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        LeaseResponse response = createLeaseService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(LeaseStatus.ACTIVE);
        assertThat(response.monthlyRent()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.currency()).isEqualTo("USD");
        verify(leaseRepository).save(any(Lease.class));
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException quand la propriété a déjà un bail actif")
    void shouldThrowWhenPropertyHasActiveLease() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31),
                5, null
        );

        Lease existingLease = new Lease(
                UUID.randomUUID(), propertyId, null, tenantId, null, landlordId, null,
                LeaseStatus.ACTIVE, new BigDecimal("500.00"), "USD", new BigDecimal("1000.00"),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 5, null,
                null, null, null, Instant.now(), Instant.now(), null
        );

        when(leaseRepository.findActiveByPropertyId(propertyId)).thenReturn(Optional.of(existingLease));

        assertThatThrownBy(() -> createLeaseService.execute(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("bail actif");

        verify(leaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever ValidationException quand la date de fin est avant la date de début")
    void shouldThrowWhenEndDateBeforeStartDate() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2027, 4, 1), LocalDate.of(2026, 3, 31),
                5, null
        );

        assertThatThrownBy(() -> createLeaseService.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("date de fin");

        verify(leaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit générer l'échéancier de paiement lors de la création du bail")
    void shouldGeneratePaymentScheduleOnCreate() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 30),
                5, null
        );

        when(leaseRepository.findActiveByPropertyId(propertyId)).thenReturn(Optional.empty());
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> {
            Lease lease = invocation.getArgument(0);
            return new Lease(
                    UUID.randomUUID(), lease.propertyId(), lease.propertyTitle(),
                    lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                    lease.status(), lease.monthlyRent(), lease.currency(), lease.depositAmount(),
                    lease.startDate(), lease.endDate(), lease.paymentDayOfMonth(), lease.notes(),
                    null, null, lease.companyId(), Instant.now(), Instant.now(), null
            );
        });

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PaymentSchedule>> captor = ArgumentCaptor.forClass(List.class);
        when(scheduleRepository.saveAll(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        createLeaseService.execute(request);

        List<PaymentSchedule> schedules = captor.getValue();
        assertThat(schedules).isNotEmpty();
        assertThat(schedules).allSatisfy(s -> {
            assertThat(s.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
        });
    }

    @Test
    @DisplayName("Doit publier un LeaseCreatedEvent après la création du bail")
    void shouldPublishLeaseCreatedEvent() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31),
                5, null
        );

        when(leaseRepository.findActiveByPropertyId(propertyId)).thenReturn(Optional.empty());
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> {
            Lease lease = invocation.getArgument(0);
            return new Lease(
                    UUID.randomUUID(), lease.propertyId(), lease.propertyTitle(),
                    lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                    lease.status(), lease.monthlyRent(), lease.currency(), lease.depositAmount(),
                    lease.startDate(), lease.endDate(), lease.paymentDayOfMonth(), lease.notes(),
                    null, null, lease.companyId(), Instant.now(), Instant.now(), null
            );
        });
        when(scheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        createLeaseService.execute(request);

        ArgumentCaptor<LeaseCreatedEvent> eventCaptor = ArgumentCaptor.forClass(LeaseCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        LeaseCreatedEvent event = eventCaptor.getValue();
        assertThat(event.propertyId()).isEqualTo(propertyId);
        assertThat(event.tenantId()).isEqualTo(tenantId);
        assertThat(event.landlordId()).isEqualTo(landlordId);
    }

    @Test
    @DisplayName("Doit créer le bail avec le statut ACTIVE")
    void shouldCreateLeaseWithActiveStatus() {
        CreateLeaseRequest request = new CreateLeaseRequest(
                propertyId, tenantId, landlordId,
                new BigDecimal("500.00"), new BigDecimal("1000.00"),
                "USD", LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31),
                5, null
        );

        when(leaseRepository.findActiveByPropertyId(propertyId)).thenReturn(Optional.empty());

        ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);
        when(leaseRepository.save(leaseCaptor.capture())).thenAnswer(invocation -> {
            Lease lease = invocation.getArgument(0);
            return new Lease(
                    UUID.randomUUID(), lease.propertyId(), lease.propertyTitle(),
                    lease.tenantId(), lease.tenantName(), lease.landlordId(), lease.landlordName(),
                    lease.status(), lease.monthlyRent(), lease.currency(), lease.depositAmount(),
                    lease.startDate(), lease.endDate(), lease.paymentDayOfMonth(), lease.notes(),
                    null, null, lease.companyId(), Instant.now(), Instant.now(), null
            );
        });
        when(scheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        createLeaseService.execute(request);

        Lease savedLease = leaseCaptor.getValue();
        assertThat(savedLease.status()).isEqualTo(LeaseStatus.ACTIVE);
    }
}
