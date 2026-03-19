package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.RenewLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.event.rent.LeaseRenewedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenewLeaseServiceTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RenewLeaseService renewLeaseService;

    private UUID leaseId;
    private Lease activeLease;

    @BeforeEach
    void setUp() {
        leaseId = UUID.randomUUID();

        activeLease = new Lease(
                leaseId, UUID.randomUUID(), "Appartement A",
                UUID.randomUUID(), "Locataire", UUID.randomUUID(), "Propriétaire",
                LeaseStatus.ACTIVE, new BigDecimal("500.00"), "USD", new BigDecimal("1000.00"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                null, null, UUID.randomUUID(), Instant.now(), Instant.now(), null
        );
    }

    @Test
    @DisplayName("Doit renouveler un bail actif avec succès")
    void shouldRenewActiveLease() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2027, 12, 31), null
        );

        LeaseResponse response = renewLeaseService.execute(leaseId, request);

        assertThat(response.status()).isEqualTo(LeaseStatus.ACTIVE);
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2027, 12, 31));
        assertThat(response.monthlyRent()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour un bail résilié")
    void shouldThrowWhenLeaseIsTerminated() {
        Lease terminatedLease = new Lease(
                leaseId, UUID.randomUUID(), null, UUID.randomUUID(), null, UUID.randomUUID(), null,
                LeaseStatus.TERMINATED, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                Instant.now(), "Résilié", UUID.randomUUID(), Instant.now(), Instant.now(), null
        );

        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(terminatedLease));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2027, 12, 31), null
        );

        assertThatThrownBy(() -> renewLeaseService.execute(leaseId, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("renouvelés");

        verify(leaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit appliquer le nouveau loyer si spécifié")
    void shouldApplyNewMonthlyRent() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));

        ArgumentCaptor<Lease> captor = ArgumentCaptor.forClass(Lease.class);
        when(leaseRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2027, 12, 31), new BigDecimal("600.00")
        );

        renewLeaseService.execute(leaseId, request);

        Lease saved = captor.getValue();
        assertThat(saved.monthlyRent()).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    @DisplayName("Doit conserver l'ancien loyer si aucun nouveau n'est spécifié")
    void shouldKeepOldRentWhenNoNewRentProvided() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));

        ArgumentCaptor<Lease> captor = ArgumentCaptor.forClass(Lease.class);
        when(leaseRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2027, 12, 31), null
        );

        renewLeaseService.execute(leaseId, request);

        Lease saved = captor.getValue();
        assertThat(saved.monthlyRent()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Doit lever ValidationException si la nouvelle date de fin est avant l'ancienne")
    void shouldThrowWhenNewEndDateBeforeCurrentEndDate() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2026, 6, 30), null
        );

        assertThatThrownBy(() -> renewLeaseService.execute(leaseId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    @DisplayName("Doit publier un LeaseRenewedEvent après le renouvellement")
    void shouldPublishLeaseRenewedEvent() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2027, 12, 31), null
        );

        renewLeaseService.execute(leaseId, request);

        ArgumentCaptor<LeaseRenewedEvent> eventCaptor = ArgumentCaptor.forClass(LeaseRenewedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        LeaseRenewedEvent event = eventCaptor.getValue();
        assertThat(event.leaseId()).isEqualTo(leaseId);
        assertThat(event.newEndDate()).isEqualTo(LocalDate.of(2027, 12, 31));
    }

    @Test
    @DisplayName("Doit permettre le renouvellement d'un bail expiré")
    void shouldAllowRenewalOfExpiredLease() {
        Lease expiredLease = new Lease(
                leaseId, UUID.randomUUID(), null, UUID.randomUUID(), null, UUID.randomUUID(), null,
                LeaseStatus.EXPIRED, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 5, null,
                null, null, UUID.randomUUID(), Instant.now(), Instant.now(), null
        );

        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(expiredLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RenewLeaseRequest request = new RenewLeaseRequest(
                LocalDate.of(2026, 12, 31), new BigDecimal("550.00")
        );

        LeaseResponse response = renewLeaseService.execute(leaseId, request);
        assertThat(response.status()).isEqualTo(LeaseStatus.ACTIVE);
    }
}
