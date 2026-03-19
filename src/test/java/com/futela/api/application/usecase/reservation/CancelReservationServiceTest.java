package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.CancelReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.event.ReservationCancelledEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelReservationServiceTest {

    @Mock
    private JpaReservationRepository reservationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CancelReservationService service;

    private UUID reservationId;
    private ReservationEntity reservation;
    private PropertyEntity property;
    private UserEntity user;
    private UserEntity host;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();

        user = new UserEntity();
        user.setFirstName("Guest");
        user.setLastName("Test");

        host = new UserEntity();
        host.setFirstName("Host");
        host.setLastName("Test");

        property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(UUID.randomUUID());
        lenient().when(property.getTitle()).thenReturn("Test Property");

        reservation = new ReservationEntity();
        reservation.setProperty(property);
        reservation.setUser(user);
        reservation.setHost(host);
    }

    @Test
    @DisplayName("Doit annuler une réservation en attente avec succès")
    void shouldCancelPendingReservationSuccessfully() {
        reservation.setStatus(ReservationStatus.PENDING);
        CancelReservationRequest request = new CancelReservationRequest("Changement de plans");

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId, request);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(reservation.getCancelReason()).isEqualTo("Changement de plans");
        assertThat(reservation.getCancelledAt()).isNotNull();
        verify(eventPublisher).publishEvent(any(ReservationCancelledEvent.class));
    }

    @Test
    @DisplayName("Doit annuler une réservation confirmée avec succès")
    void shouldCancelConfirmedReservationSuccessfully() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        CancelReservationRequest request = new CancelReservationRequest("Urgence");

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId, request);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(reservation.getCancelReason()).isEqualTo("Urgence");
        verify(eventPublisher).publishEvent(any(ReservationCancelledEvent.class));
    }

    @Test
    @DisplayName("Doit rejeter l'annulation d'une réservation terminée")
    void shouldRejectCancellingCompletedReservation() {
        reservation.setStatus(ReservationStatus.COMPLETED);
        CancelReservationRequest request = new CancelReservationRequest("Trop tard");

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidOperationException.class, () -> service.execute(reservationId, request));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit accepter l'annulation sans raison (raison optionnelle)")
    void shouldAcceptCancelWithoutReason() {
        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId, null);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(eventPublisher).publishEvent(any(ReservationCancelledEvent.class));
    }

    @Test
    @DisplayName("Doit rejeter quand la réservation n'existe pas")
    void shouldRejectWhenReservationNotFound() {
        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.execute(reservationId, new CancelReservationRequest("Raison")));
    }

    @Test
    @DisplayName("Doit rejeter l'annulation d'une réservation déjà annulée")
    void shouldRejectCancellingAlreadyCancelledReservation() {
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.execute(reservationId, new CancelReservationRequest("Raison")))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("déjà annulée");
    }

    @Test
    @DisplayName("Doit publier un événement ReservationCancelledEvent avec la raison")
    void shouldPublishCancelledEventWithReason() {
        reservation.setStatus(ReservationStatus.PENDING);
        CancelReservationRequest request = new CancelReservationRequest("Motif important");

        when(reservationRepository.findByIdAndDeletedAtIsNull(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(reservationId, request);

        ArgumentCaptor<ReservationCancelledEvent> eventCaptor = ArgumentCaptor.forClass(ReservationCancelledEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().reason()).isEqualTo("Motif important");
    }
}
