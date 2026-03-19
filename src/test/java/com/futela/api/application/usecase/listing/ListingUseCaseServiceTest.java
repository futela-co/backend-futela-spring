package com.futela.api.application.usecase.listing;

import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.enums.ListingType;
import com.futela.api.domain.enums.PropertyStatus;
import com.futela.api.domain.enums.PropertyType;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Listing;
import com.futela.api.domain.model.property.Property;
import com.futela.api.domain.port.out.property.ListingRepositoryPort;
import com.futela.api.domain.port.out.property.PropertyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingUseCaseServiceTest {

    @Mock
    private ListingRepositoryPort listingRepository;

    @Mock
    private PropertyRepositoryPort propertyRepository;

    @InjectMocks
    private ListingUseCaseService service;

    private UUID userId;
    private UUID propertyId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        companyId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Doit ajouter une propriete aux favoris avec succes")
    void shouldAddToFavoritesSuccessfully() {
        var property = createDomainProperty(propertyId);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(listingRepository.existsByUserIdAndPropertyId(userId, propertyId)).thenReturn(false);

        service.addToFavorites(userId, propertyId, companyId);

        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    @DisplayName("Doit rejeter l'ajout en double aux favoris")
    void shouldRejectDuplicateFavorite() {
        var property = createDomainProperty(propertyId);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(listingRepository.existsByUserIdAndPropertyId(userId, propertyId)).thenReturn(true);

        assertThatThrownBy(() -> service.addToFavorites(userId, propertyId, companyId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("favoris");

        verify(listingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit rejeter l'ajout aux favoris d'une propriete inexistante")
    void shouldRejectAddNonExistentPropertyToFavorites() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addToFavorites(userId, propertyId, companyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Propriété");
    }

    @Test
    @DisplayName("Doit retirer une propriete des favoris avec succes")
    void shouldRemoveFromFavoritesSuccessfully() {
        when(listingRepository.existsByUserIdAndPropertyId(userId, propertyId)).thenReturn(true);

        service.removeFromFavorites(userId, propertyId);

        verify(listingRepository).deleteByUserIdAndPropertyId(userId, propertyId);
    }

    @Test
    @DisplayName("Doit rejeter le retrait d'un favori inexistant")
    void shouldRejectRemoveNonExistentFavorite() {
        when(listingRepository.existsByUserIdAndPropertyId(userId, propertyId)).thenReturn(false);

        assertThatThrownBy(() -> service.removeFromFavorites(userId, propertyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Favori");
    }

    @Test
    @DisplayName("Doit retourner la liste des favoris de l'utilisateur")
    void shouldReturnUserFavorites() {
        var listing1 = new Listing(UUID.randomUUID(), userId, propertyId, companyId, Instant.now());
        UUID propertyId2 = UUID.randomUUID();
        var listing2 = new Listing(UUID.randomUUID(), userId, propertyId2, companyId, Instant.now());

        var property1 = createDomainProperty(propertyId);
        var property2 = createDomainProperty(propertyId2);

        when(listingRepository.findByUserId(userId)).thenReturn(List.of(listing1, listing2));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));
        when(propertyRepository.findById(propertyId2)).thenReturn(Optional.of(property2));

        List<PropertySummaryResponse> result = service.getUserFavorites(userId);

        assertThat(result).hasSize(2);
        verify(listingRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Doit retourner une liste vide quand l'utilisateur n'a pas de favoris")
    void shouldReturnEmptyListWhenNoFavorites() {
        when(listingRepository.findByUserId(userId)).thenReturn(List.of());

        List<PropertySummaryResponse> result = service.getUserFavorites(userId);

        assertThat(result).isEmpty();
    }

    private Property createDomainProperty(UUID id) {
        return new Property(
                id, "Test Property", "Description", PropertyType.APARTMENT, PropertyStatus.PUBLISHED,
                ListingType.RENT, BigDecimal.valueOf(50), null, null,
                "test-slug-" + id.toString().substring(0, 8), true, true, true, 0, null, 0,
                UUID.randomUUID(), "Owner Name", null, null, UUID.randomUUID(), companyId,
                2, 1, 60, null, null, null, null, null, null,
                null, null, null, null, List.of(), null,
                Instant.now(), Instant.now()
        );
    }
}
