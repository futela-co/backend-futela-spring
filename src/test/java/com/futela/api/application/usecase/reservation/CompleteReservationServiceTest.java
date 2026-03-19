package com.futela.api.application.usecase.reservation;

import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteReservationServiceTest {

    @Mock
    private JpaReservationRepository reservationRepository;

    @InjectMocks
    private CompleteReservationService service;

    private UUID reservationId;
    private ReservationEntity reservation;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setFirstName("Guest");
        user.setLastName("Test");

        UserEntity host = new UserEntity();
        host.setFirstName("Host");
        host.setLastName("Test");

        PropertyEntity property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(UUID.randomUUID());
        lenient().when(property.getTitle()).thenReturn("Test Property");

        reservation = new ReservationEntity();
        reservation.setProperty(property);
        reservation.setUser(user);
        reservation.setHost(host);
    }

    @Test
    @DisplayName("Doit terminer une réservation confirmée avec succès")
    void shouldCompleteConfirmedReservationSuccessfully() {
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(reservation.getCompletedAt()).isNotNull();
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("Doit rejeter la complétion d'une réservation en attente")
    void shouldRejectCompletingPendingReservation() {
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidOperationException.class, () -> service.execute(reservationId));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit rejeter la complétion d'une réservation annulée")
    void shouldRejectCompletingCancelledReservation() {
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.execute(reservationId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("confirmées");
    }

    @Test
    @DisplayName("Doit rejeter quand la réservation n'existe pas")
    void shouldRejectWhenReservationNotFound() {
        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(reservationId));
    }

    @Test
    @DisplayName("Doit définir la date de complétion lors de la terminaison")
    void shouldSetCompletedAtTimestamp() {
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId);

        assertThat(reservation.getCompletedAt()).isNotNull();
    }
}
