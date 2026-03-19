package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentInvoiceGeneratedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateRentInvoiceServiceTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private RentInvoiceRepositoryPort invoiceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GenerateRentInvoiceService generateRentInvoiceService;

    private UUID leaseId;
    private Lease activeLease;

    @BeforeEach
    void setUp() {
        leaseId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        activeLease = new Lease(
                leaseId, UUID.randomUUID(), "Appartement A",
                UUID.randomUUID(), "Locataire", UUID.randomUUID(), "Propriétaire",
                LeaseStatus.ACTIVE, new BigDecimal("500.00"), "USD", new BigDecimal("1000.00"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                null, null, companyId, Instant.now(), Instant.now(), null
        );
    }

    @Test
    @DisplayName("Doit générer une facture pour un bail actif")
    void shouldGenerateInvoiceForActiveLease() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(invoiceRepository.findByLeaseIdAndPeriod(eq(leaseId), any(), any())).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> {
            RentInvoice invoice = invocation.getArgument(0);
            return new RentInvoice(
                    UUID.randomUUID(), invoice.leaseId(), invoice.invoiceNumber(),
                    invoice.amount(), invoice.paidAmount(), invoice.status(),
                    invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                    invoice.lateFee(), invoice.companyId(), Instant.now(), Instant.now()
            );
        });

        RentInvoiceResponse response = generateRentInvoiceService.execute(leaseId, 3, 2026);

        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        verify(invoiceRepository).save(any(RentInvoice.class));
    }

    @Test
    @DisplayName("Doit générer un numéro de facture au format FUT-YYYY-MMDD-####")
    void shouldGenerateInvoiceNumberInCorrectFormat() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(invoiceRepository.findByLeaseIdAndPeriod(eq(leaseId), any(), any())).thenReturn(Optional.empty());

        ArgumentCaptor<RentInvoice> captor = ArgumentCaptor.forClass(RentInvoice.class);
        when(invoiceRepository.save(captor.capture())).thenAnswer(invocation -> {
            RentInvoice invoice = invocation.getArgument(0);
            return new RentInvoice(
                    UUID.randomUUID(), invoice.leaseId(), invoice.invoiceNumber(),
                    invoice.amount(), invoice.paidAmount(), invoice.status(),
                    invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                    invoice.lateFee(), invoice.companyId(), Instant.now(), Instant.now()
            );
        });

        generateRentInvoiceService.execute(leaseId, 3, 2026);

        RentInvoice saved = captor.getValue();
        assertThat(saved.invoiceNumber()).matches("FUT-2026-03\\d{2}-[A-Z0-9]{4}");
    }

    @Test
    @DisplayName("Doit créer la facture avec le montant égal au loyer mensuel")
    void shouldSetInvoiceAmountToMonthlyRent() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(invoiceRepository.findByLeaseIdAndPeriod(eq(leaseId), any(), any())).thenReturn(Optional.empty());

        ArgumentCaptor<RentInvoice> captor = ArgumentCaptor.forClass(RentInvoice.class);
        when(invoiceRepository.save(captor.capture())).thenAnswer(invocation -> {
            RentInvoice invoice = invocation.getArgument(0);
            return new RentInvoice(
                    UUID.randomUUID(), invoice.leaseId(), invoice.invoiceNumber(),
                    invoice.amount(), invoice.paidAmount(), invoice.status(),
                    invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                    invoice.lateFee(), invoice.companyId(), Instant.now(), Instant.now()
            );
        });

        generateRentInvoiceService.execute(leaseId, 3, 2026);

        assertThat(captor.getValue().amount()).isEqualByComparingTo(activeLease.monthlyRent());
    }

    @Test
    @DisplayName("Doit publier un RentInvoiceGeneratedEvent après la génération")
    void shouldPublishRentInvoiceGeneratedEvent() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));
        when(invoiceRepository.findByLeaseIdAndPeriod(eq(leaseId), any(), any())).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> {
            RentInvoice invoice = invocation.getArgument(0);
            return new RentInvoice(
                    UUID.randomUUID(), invoice.leaseId(), invoice.invoiceNumber(),
                    invoice.amount(), invoice.paidAmount(), invoice.status(),
                    invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                    invoice.lateFee(), invoice.companyId(), Instant.now(), Instant.now()
            );
        });

        generateRentInvoiceService.execute(leaseId, 3, 2026);

        ArgumentCaptor<RentInvoiceGeneratedEvent> eventCaptor = ArgumentCaptor.forClass(RentInvoiceGeneratedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        RentInvoiceGeneratedEvent event = eventCaptor.getValue();
        assertThat(event.leaseId()).isEqualTo(leaseId);
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour un bail inactif")
    void shouldThrowWhenLeaseIsNotActive() {
        Lease terminatedLease = new Lease(
                leaseId, UUID.randomUUID(), null, UUID.randomUUID(), null, UUID.randomUUID(), null,
                LeaseStatus.TERMINATED, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                Instant.now(), "Fin de contrat", null, Instant.now(), Instant.now(), null
        );

        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(terminatedLease));

        assertThatThrownBy(() -> generateRentInvoiceService.execute(leaseId, 3, 2026))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("bail inactif");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException si une facture existe déjà pour la période")
    void shouldThrowWhenInvoiceAlreadyExistsForPeriod() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(activeLease));

        RentInvoice existingInvoice = new RentInvoice(
                UUID.randomUUID(), leaseId, "FUT-2026-0301-ABCD",
                new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING,
                LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, null, Instant.now(), Instant.now()
        );

        when(invoiceRepository.findByLeaseIdAndPeriod(eq(leaseId), any(), any()))
                .thenReturn(Optional.of(existingInvoice));

        assertThatThrownBy(() -> generateRentInvoiceService.execute(leaseId, 3, 2026))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("facture existe déjà");
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException pour un bail inexistant")
    void shouldThrowWhenLeaseNotFound() {
        when(leaseRepository.findById(leaseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generateRentInvoiceService.execute(leaseId, 3, 2026))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
