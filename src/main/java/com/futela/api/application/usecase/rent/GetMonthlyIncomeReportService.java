package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.MonthlyIncomeResponse;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.port.in.rent.GetMonthlyIncomeReportUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetMonthlyIncomeReportService implements GetMonthlyIncomeReportUseCase {

    private final RentInvoiceRepositoryPort invoiceRepository;
    private final LeaseRepositoryPort leaseRepository;

    public GetMonthlyIncomeReportService(RentInvoiceRepositoryPort invoiceRepository, LeaseRepositoryPort leaseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public MonthlyIncomeResponse execute(UUID landlordId, int year) {
        var invoices = invoiceRepository.findByLandlordId(landlordId);
        List<MonthlyIncomeResponse.MonthBreakdown> months = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            final int month = m;
            var monthInvoices = invoices.stream()
                    .filter(i -> i.periodStart().getYear() == year && i.periodStart().getMonthValue() == month)
                    .toList();

            BigDecimal income = monthInvoices.stream()
                    .filter(i -> i.status() == PaymentStatus.PAID)
                    .map(i -> i.paidAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long paid = monthInvoices.stream().filter(i -> i.status() == PaymentStatus.PAID).count();
            long overdue = monthInvoices.stream().filter(i -> i.status() == PaymentStatus.OVERDUE).count();

            total = total.add(income);
            months.add(new MonthlyIncomeResponse.MonthBreakdown(
                    month, Month.of(month).name(), income, paid, overdue
            ));
        }

        return new MonthlyIncomeResponse(year, total, months);
    }
}
