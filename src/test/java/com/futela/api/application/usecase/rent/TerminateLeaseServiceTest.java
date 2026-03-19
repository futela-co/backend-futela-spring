package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.TerminateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.event.rent.LeaseTerminatedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
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
class TerminateLeaseServiceTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TerminateLeaseService terminateLeaseService;

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
    @DisplayName("Doit résilier un bail actif avec succès")
    void shouldTerminateActiveLease() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TerminateLeaseRequest request = new TerminateLeaseRequest("Fin de contrat anticipée");

        LeaseResponse response = terminateLeaseService.execute(leaseId, request);

        assertThat(response.status()).isEqualTo(LeaseStatus.TERMINATED);
        assertThat(response.terminationReason()).isEqualTo("Fin de contrat anticipée");
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour un bail déjà résilié")
    void shouldThrowWhenLeaseAlreadyTerminated() {
        Lease terminatedLease = new Lease(
                leaseId, UUID.randomUUID(), null, UUID.randomUUID(), null, UUID.randomUUID(), null,
                LeaseStatus.TERMINATED, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                Instant.now(), "Déjà résilié", UUID.randomUUID(), Instant.now(), Instant.now(), null
        );

        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(terminatedLease));

        TerminateLeaseRequest request = new TerminateLeaseRequest("Autre raison");

        assertThatThrownBy(() -> terminateLeaseService.execute(leaseId, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("baux actifs");

        verify(leaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit sauvegarder la raison de résiliation")
    void shouldSaveTerminationReason() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));

        ArgumentCaptor<Lease> captor = ArgumentCaptor.forClass(Lease.class);
        when(leaseRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        TerminateLeaseRequest request = new TerminateLeaseRequest("Non-paiement du loyer");

        terminateLeaseService.execute(leaseId, request);

        Lease saved = captor.getValue();
        assertThat(saved.terminationReason()).isEqualTo("Non-paiement du loyer");
        assertThat(saved.terminatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Doit publier un LeaseTerminatedEvent après la résiliation")
    void shouldPublishLeaseTerminatedEvent() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TerminateLeaseRequest request = new TerminateLeaseRequest("Fin anticipée");

        terminateLeaseService.execute(leaseId, request);

        ArgumentCaptor<LeaseTerminatedEvent> eventCaptor = ArgumentCaptor.forClass(LeaseTerminatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        LeaseTerminatedEvent event = eventCaptor.getValue();
        assertThat(event.leaseId()).isEqualTo(leaseId);
        assertThat(event.reason()).isEqualTo("Fin anticipée");
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException pour un bail inexistant")
    void shouldThrowWhenLeaseNotFound() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.empty());

        TerminateLeaseRequest request = new TerminateLeaseRequest("Raison");

        assertThatThrownBy(() -> terminateLeaseService.execute(leaseId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
