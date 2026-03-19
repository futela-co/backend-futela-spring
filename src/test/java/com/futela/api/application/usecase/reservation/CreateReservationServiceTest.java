package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.CreateReservationRequest;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import jakarta.persistence.EntityManager;
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
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CreateReservationServiceTest {

    @Mock
    private JpaReservationRepository reservationRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateReservationService service;

    private UUID userId;
    private UUID propertyId;
    private PropertyEntity property;
    private UserEntity user;
    private UserEntity owner;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        company = new CompanyEntity();
        company.setName("Test Company");

        owner = new UserEntity();
        owner.setFirstName("Owner");
        owner.setLastName("Test");

        user = new UserEntity();
        user.setFirstName("Guest");
        user.setLastName("Test");
    }

    @Test
    @DisplayName("Doit rejeter quand la date de fin est avant la date de début")
    void shouldRejectWhenEndDateBeforeStartDate() {
        CreateReservationRequest request = new CreateReservationRequest(
                propertyId,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2),
                2,
                null
        );

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    @DisplayName("Doit rejeter quand la propriété n'existe pas")
    void shouldRejectWhenPropertyNotFound() {
        CreateReservationRequest request = new CreateReservationRequest(
                propertyId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                2,
                null
        );

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(null);

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit rejeter quand il y a chevauchement de dates")
    void shouldRejectWhenDatesOverlap() {
        CreateReservationRequest request = new CreateReservationRequest(
                propertyId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                2,
                null
        );

        // PropertyEntity is abstract, so we need a concrete subclass for testing
        // Using mock instead
        PropertyEntity mockProperty = mock(PropertyEntity.class);
        lenient().when(mockProperty.getOwner()).thenReturn(owner);
        lenient().when(mockProperty.getCompany()).thenReturn(company);
        lenient().when(mockProperty.getPricePerDay()).thenReturn(BigDecimal.valueOf(100));

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(mockProperty);
        lenient().when(entityManager.find(UserEntity.class, userId)).thenReturn(user);
        when(reservationRepository.existsOverlapping(eq(propertyId), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.execute(request, userId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("disponible");
    }

    @Test
    @DisplayName("Doit calculer le prix total correctement")
    void shouldCalculateTotalPriceCorrectly() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(4); // 3 days

        CreateReservationRequest request = new CreateReservationRequest(
                propertyId, startDate, endDate, 2, "Test notes"
        );

        PropertyEntity mockProperty = mock(PropertyEntity.class);
        when(mockProperty.getId()).thenReturn(propertyId);
        when(mockProperty.getTitle()).thenReturn("Test Property");
        when(mockProperty.getOwner()).thenReturn(owner);
        when(mockProperty.getCompany()).thenReturn(company);
        when(mockProperty.getPricePerDay()).thenReturn(BigDecimal.valueOf(50));

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(mockProperty);
        when(entityManager.find(UserEntity.class, userId)).thenReturn(user);
        when(reservationRepository.existsOverlapping(any(), any(), any(), any())).thenReturn(false);

        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        when(reservationRepository.save(captor.capture())).thenAnswer(invocation -> {
            ReservationEntity entity = invocation.getArgument(0);
            // Simulate JPA setting the ID
            var idField = entity.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, UUID.randomUUID());
            var createdAtField = entity.getClass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, java.time.Instant.now());
            var updatedAtField = entity.getClass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(entity, java.time.Instant.now());
            return entity;
        });

        ReservationResponse response = service.execute(request, userId);

        assertThat(response).isNotNull();
        assertThat(captor.getValue().getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150)); // 3 * 50
        assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(captor.getValue().getGuestCount()).isEqualTo(2);
    }
}
