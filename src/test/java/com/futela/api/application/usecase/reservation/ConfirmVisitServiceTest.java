package com.futela.api.application.usecase.reservation;

import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.event.VisitConfirmedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmVisitServiceTest {

    @Mock
    private JpaVisitRepository visitRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ConfirmVisitService service;

    private UUID visitId;
    private VisitEntity visit;

    @BeforeEach
    void setUp() {
        visitId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setFirstName("Visitor");
        user.setLastName("Test");

        PropertyEntity property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(UUID.randomUUID());
        lenient().when(property.getTitle()).thenReturn("Test Property");

        visit = new VisitEntity();
        visit.setProperty(property);
        visit.setUser(user);
        visit.setScheduledAt(Instant.now().plusSeconds(86400));
    }

    @Test
    @DisplayName("Doit confirmer une visite planifiée avec succès")
    void shouldConfirmScheduledVisitSuccessfully() {
        visit.setStatus(VisitStatus.SCHEDULED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(visitId);

        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CONFIRMED);
        assertThat(visit.getConfirmedAt()).isNotNull();
        verify(visitRepository).save(visit);
        verify(eventPublisher).publishEvent(any(VisitConfirmedEvent.class));
    }

    @Test
    @DisplayName("Doit rejeter la confirmation d'une visite déjà confirmée")
    void shouldRejectConfirmingAlreadyConfirmedVisit() {
        visit.setStatus(VisitStatus.CONFIRMED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.execute(visitId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("planifiées");
    }

    @Test
    @DisplayName("Doit rejeter la confirmation d'une visite annulée")
    void shouldRejectConfirmingCancelledVisit() {
        visit.setStatus(VisitStatus.CANCELLED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));

        assertThrows(InvalidOperationException.class, () -> service.execute(visitId));
    }

    @Test
    @DisplayName("Doit rejeter quand la visite n'existe pas")
    void shouldRejectWhenVisitNotFound() {
        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(visitId));
    }

    @Test
    @DisplayName("Doit publier un événement VisitConfirmedEvent après la confirmation")
    void shouldPublishVisitConfirmedEvent() {
        visit.setStatus(VisitStatus.SCHEDULED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(visitId);

        ArgumentCaptor<VisitConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(VisitConfirmedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
    }
}
