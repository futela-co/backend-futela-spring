package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LandlordDashboardResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLandlordDashboardServiceTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private RentInvoiceRepositoryPort invoiceRepository;

    @Mock
    private RentPaymentRepositoryPort paymentRepository;

    @InjectMocks
    private GetLandlordDashboardService dashboardService;

    private UUID landlordId;

    @BeforeEach
    void setUp() {
        landlordId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Doit calculer les KPIs du tableau de bord du propriétaire")
    void shouldCalculateDashboardKPIs() {
        Lease activeLease1 = new Lease(
                UUID.randomUUID(), UUID.randomUUID(), null, UUID.randomUUID(), null, landlordId, null,
                LeaseStatus.ACTIVE, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                null, null, null, Instant.now(), Instant.now(), null
        );
        Lease activeLease2 = new Lease(
                UUID.randomUUID(), UUID.randomUUID(), null, UUID.randomUUID(), null, landlordId, null,
                LeaseStatus.ACTIVE, new BigDecimal("300.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                null, null, null, Instant.now(), Instant.now(), null
        );
        Lease terminatedLease = new Lease(
                UUID.randomUUID(), UUID.randomUUID(), null, UUID.randomUUID(), null, landlordId, null,
                LeaseStatus.TERMINATED, new BigDecimal("400.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 5, null,
                Instant.now(), "Fin", null, Instant.now(), Instant.now(), null
        );

        when(leaseRepository.countByLandlordId(landlordId)).thenReturn(3L);
        when(leaseRepository.countActiveByLandlordId(landlordId)).thenReturn(2L);
        when(leaseRepository.findByLandlordId(landlordId)).thenReturn(List.of(activeLease1, activeLease2, terminatedLease));
        when(paymentRepository.sumByLandlordIdAndYear(eq(landlordId), eq(Year.now().getValue())))
                .thenReturn(new BigDecimal("4800.00"));
        when(invoiceRepository.countByLandlordIdAndStatus(landlordId, PaymentStatus.OVERDUE)).thenReturn(1L);
        when(invoiceRepository.countByLandlordIdAndStatus(landlordId, PaymentStatus.PENDING)).thenReturn(2L);

        LandlordDashboardResponse response = dashboardService.execute(landlordId);

        assertThat(response.totalProperties()).isEqualTo(3);
        assertThat(response.propertiesRented()).isEqualTo(2);
        assertThat(response.monthlyIncome()).isEqualByComparingTo(new BigDecimal("800.00")); // 500 + 300
        assertThat(response.yearlyIncome()).isEqualByComparingTo(new BigDecimal("4800.00"));
        assertThat(response.overduePayments()).isEqualTo(1);
        assertThat(response.pendingPayments()).isEqualTo(2);
    }

    @Test
    @DisplayName("Doit calculer le taux d'occupation correctement")
    void shouldCalculateOccupancyRate() {
        when(leaseRepository.countByLandlordId(landlordId)).thenReturn(4L);
        when(leaseRepository.countActiveByLandlordId(landlordId)).thenReturn(3L);
        when(leaseRepository.findByLandlordId(landlordId)).thenReturn(List.of());
        when(paymentRepository.sumByLandlordIdAndYear(eq(landlordId), eq(Year.now().getValue())))
                .thenReturn(BigDecimal.ZERO);
        when(invoiceRepository.countByLandlordIdAndStatus(eq(landlordId), any())).thenReturn(0L);

        LandlordDashboardResponse response = dashboardService.execute(landlordId);

        assertThat(response.occupancyRate()).isEqualTo(75.0); // 3/4 * 100
    }

    @Test
    @DisplayName("Doit retourner un taux d'occupation de 0 quand aucune propriété")
    void shouldReturnZeroOccupancyWhenNoProperties() {
        when(leaseRepository.countByLandlordId(landlordId)).thenReturn(0L);
        when(leaseRepository.countActiveByLandlordId(landlordId)).thenReturn(0L);
        when(leaseRepository.findByLandlordId(landlordId)).thenReturn(List.of());
        when(paymentRepository.sumByLandlordIdAndYear(eq(landlordId), eq(Year.now().getValue())))
                .thenReturn(BigDecimal.ZERO);
        when(invoiceRepository.countByLandlordIdAndStatus(eq(landlordId), any())).thenReturn(0L);

        LandlordDashboardResponse response = dashboardService.execute(landlordId);

        assertThat(response.occupancyRate()).isEqualTo(0.0);
        assertThat(response.totalProperties()).isEqualTo(0);
    }
}
