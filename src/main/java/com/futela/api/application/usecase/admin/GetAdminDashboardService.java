package com.futela.api.application.usecase.admin;

import com.futela.api.application.dto.response.admin.AdminDashboardResponse;
import com.futela.api.domain.port.in.admin.GetAdminDashboardUseCase;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import com.futela.api.domain.port.out.property.PropertyRepositoryPort;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import com.futela.api.domain.port.out.reservation.ReservationRepositoryPort;
import com.futela.api.domain.port.out.review.ReviewRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAdminDashboardService implements GetAdminDashboardUseCase {

    private final UserRepositoryPort userRepository;
    private final PropertyRepositoryPort propertyRepository;
    private final ReservationRepositoryPort reservationRepository;
    private final ReviewRepositoryPort reviewRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final LeaseRepositoryPort leaseRepository;

    public GetAdminDashboardService(UserRepositoryPort userRepository,
                                    PropertyRepositoryPort propertyRepository,
                                    ReservationRepositoryPort reservationRepository,
                                    ReviewRepositoryPort reviewRepository,
                                    TransactionRepositoryPort transactionRepository,
                                    LeaseRepositoryPort leaseRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
        this.transactionRepository = transactionRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    public AdminDashboardResponse execute() {
        long totalUsers = userRepository.countActive();
        long totalProperties = propertyRepository.countActive();
        long totalReservations = reservationRepository.countActive();
        long totalReviews = reviewRepository.countActive();
        long pendingReviews = reviewRepository.countPending();
        long totalTransactions = transactionRepository.countActive();
        long totalLeases = leaseRepository.countActive();

        return new AdminDashboardResponse(
                totalUsers,
                totalProperties,
                totalReservations,
                totalReviews,
                pendingReviews,
                totalTransactions,
                totalLeases
        );
    }
}
