package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.LandlordDashboardResponse;
import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.port.in.rent.GetLandlordDashboardUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetLandlordDashboardService implements GetLandlordDashboardUseCase {

    private final LeaseRepositoryPort leaseRepository;
    private final RentInvoiceRepositoryPort invoiceRepository;
    private final RentPaymentRepositoryPort paymentRepository;

    public GetLandlordDashboardService(LeaseRepositoryPort leaseRepository,
                                       RentInvoiceRepositoryPort invoiceRepository,
                                       RentPaymentRepositoryPort paymentRepository) {
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public LandlordDashboardResponse execute(UUID landlordId) {
        long totalProperties = leaseRepository.countByLandlordId(landlordId);
        long propertiesRented = leaseRepository.countActiveByLandlordId(landlordId);

        BigDecimal monthlyIncome = leaseRepository.findByLandlordId(landlordId).stream()
                .filter(l -> l.status() == LeaseStatus.ACTIVE)
                .map(l -> l.monthlyRent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal yearlyIncome = paymentRepository.sumByLandlordIdAndYear(landlordId, Year.now().getValue());

        long overduePayments = invoiceRepository.countByLandlordIdAndStatus(landlordId, PaymentStatus.OVERDUE);
        long pendingPayments = invoiceRepository.countByLandlordIdAndStatus(landlordId, PaymentStatus.PENDING);

        double occupancyRate = totalProperties > 0
                ? BigDecimal.valueOf(propertiesRented)
                    .divide(BigDecimal.valueOf(totalProperties), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue()
                : 0.0;

        return new LandlordDashboardResponse(
                totalProperties, propertiesRented,
                monthlyIncome, yearlyIncome,
                overduePayments, pendingPayments,
                occupancyRate
        );
    }
}
