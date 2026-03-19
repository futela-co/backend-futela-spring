package com.futela.api.application.usecase.reservation;

import com.futela.api.domain.enums.VisitStatus;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelVisitServiceTest {

    @Mock
    private JpaVisitRepository visitRepository;

    @InjectMocks
    private CancelVisitService service;

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
    @DisplayName("Doit annuler une visite planifiée avec succès")
    void shouldCancelScheduledVisitSuccessfully() {
        visit.setStatus(VisitStatus.SCHEDULED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(visitId);

        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CANCELLED);
        verify(visitRepository).save(visit);
    }

    @Test
    @DisplayName("Doit annuler une visite confirmée avec succès")
    void shouldCancelConfirmedVisitSuccessfully() {
        visit.setStatus(VisitStatus.CONFIRMED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(visitId);

        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CANCELLED);
    }

    @Test
    @DisplayName("Doit rejeter l'annulation d'une visite terminée")
    void shouldRejectCancellingCompletedVisit() {
        visit.setStatus(VisitStatus.COMPLETED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.execute(visitId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("terminée");
    }

    @Test
    @DisplayName("Doit rejeter l'annulation d'une visite déjà annulée")
    void shouldRejectCancellingAlreadyCancelledVisit() {
        visit.setStatus(VisitStatus.CANCELLED);

        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.execute(visitId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("déjà annulée");
    }

    @Test
    @DisplayName("Doit rejeter quand la visite n'existe pas")
    void shouldRejectWhenVisitNotFound() {
        when(visitRepository.findByIdAndDeletedAtIsNull(visitId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(visitId));
    }
}
