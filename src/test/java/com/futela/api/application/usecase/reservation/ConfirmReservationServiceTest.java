package com.futela.api.application.usecase.reservation;

import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmReservationServiceTest {

    @Mock
    private JpaReservationRepository reservationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ConfirmReservationService service;

    @Test
    @DisplayName("Doit rejeter quand la réservation n'existe pas")
    void shouldRejectWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(reservationRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit rejeter quand la réservation n'est pas en attente")
    void shouldRejectWhenNotPending() {
        UUID id = UUID.randomUUID();
        ReservationEntity entity = new ReservationEntity();
        entity.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("en attente");
    }
}
