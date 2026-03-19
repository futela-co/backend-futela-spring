package com.futela.api.application.usecase.property;

import com.futela.api.application.dto.request.property.CreatePropertyRequest;
import com.futela.api.application.dto.response.property.PropertyResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.enums.*;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.model.property.Property;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import com.futela.api.infrastructure.persistence.adapter.property.PropertyRepositoryAdapter;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.*;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.property.JpaCategoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyUseCaseServiceTest {

    @Mock
    private PropertyRepositoryAdapter propertyRepository;

    @Mock
    private JpaCategoryRepository categoryRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PropertyUseCaseService service;

    private UUID ownerId;
    private UUID companyId;
    private UUID propertyId;
    private UUID addressId;
    private UserEntity ownerEntity;
    private AddressEntity addressEntity;
    private CompanyEntity companyEntity;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        ownerEntity = new UserEntity();
        ownerEntity.setFirstName("John");
        ownerEntity.setLastName("Doe");

        addressEntity = new AddressEntity();
        companyEntity = new CompanyEntity();
    }

    // === Create Property ===

    @Test
    @DisplayName("Doit creer un appartement avec succes")
    void shouldCreateApartmentSuccessfully() {
        var request = new CreatePropertyRequest(
                "Bel Appartement", "Description", PropertyType.APARTMENT,
                ListingType.RENT, BigDecimal.valueOf(50), BigDecimal.valueOf(500), null,
                addressId, null, 3, 2, 80,
                null, null, null, null, null, null, null, null, null, null
        );

        when(entityManager.getReference(UserEntity.class, ownerId)).thenReturn(ownerEntity);
        when(entityManager.getReference(AddressEntity.class, addressId)).thenReturn(addressEntity);
        when(entityManager.getReference(CompanyEntity.class, companyId)).thenReturn(companyEntity);

        ApartmentEntity savedEntity = createMockApartmentEntity();
        when(propertyRepository.saveEntity(any(PropertyEntity.class))).thenReturn(savedEntity);

        PropertyResponse response = service.createProperty(request, ownerId, companyId);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Bel Appartement");
        assertThat(response.type()).isEqualTo(PropertyType.APARTMENT);
        verify(propertyRepository).saveEntity(any(ApartmentEntity.class));
    }

    @Test
    @DisplayName("Doit creer une voiture avec les champs specifiques")
    void shouldCreateCarWithSpecificFields() {
        var request = new CreatePropertyRequest(
                "Toyota Corolla", "Belle voiture", PropertyType.CAR,
                ListingType.SHORT_TERM, BigDecimal.valueOf(100), null, null,
                addressId, null, null, null, null,
                "Toyota", "Corolla", 2023, 15000, FuelType.GASOLINE, Transmission.AUTOMATIC,
                null, null, null, null
        );

        when(entityManager.getReference(UserEntity.class, ownerId)).thenReturn(ownerEntity);
        when(entityManager.getReference(AddressEntity.class, addressId)).thenReturn(addressEntity);
        when(entityManager.getReference(CompanyEntity.class, companyId)).thenReturn(companyEntity);

        CarEntity savedEntity = createMockCarEntity();
        when(propertyRepository.saveEntity(any(PropertyEntity.class))).thenReturn(savedEntity);

        PropertyResponse response = service.createProperty(request, ownerId, companyId);

        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(PropertyType.CAR);
        assertThat(response.brand()).isEqualTo("Toyota");
        assertThat(response.model()).isEqualTo("Corolla");
    }

    @Test
    @DisplayName("Doit creer un terrain avec les champs specifiques")
    void shouldCreateLandWithSpecificFields() {
        var request = new CreatePropertyRequest(
                "Terrain Residenciel", "Grand terrain", PropertyType.LAND,
                ListingType.SALE, BigDecimal.valueOf(200), null, BigDecimal.valueOf(50000),
                addressId, null, null, null, null,
                null, null, null, null, null, null,
                null, LandType.RESIDENTIAL, 1000, null
        );

        when(entityManager.getReference(UserEntity.class, ownerId)).thenReturn(ownerEntity);
        when(entityManager.getReference(AddressEntity.class, addressId)).thenReturn(addressEntity);
        when(entityManager.getReference(CompanyEntity.class, companyId)).thenReturn(companyEntity);

        LandEntity savedEntity = createMockLandEntity();
        when(propertyRepository.saveEntity(any(PropertyEntity.class))).thenReturn(savedEntity);

        PropertyResponse response = service.createProperty(request, ownerId, companyId);

        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(PropertyType.LAND);
        assertThat(response.landType()).isEqualTo(LandType.RESIDENTIAL);
        assertThat(response.surfaceArea()).isEqualTo(1000);
    }

    // === Get Property ===

    @Test
    @DisplayName("Doit retourner une propriete par son slug")
    void shouldReturnPropertyBySlug() {
        String slug = "bel-appartement-abc12345";
        var property = createDomainProperty(propertyId, "Bel Appartement", slug);

        when(propertyRepository.findBySlug(slug)).thenReturn(Optional.of(property));

        PropertyResponse response = service.getPropertyBySlug(slug);

        assertThat(response).isNotNull();
        assertThat(response.slug()).isEqualTo(slug);
        assertThat(response.title()).isEqualTo("Bel Appartement");
    }

    @Test
    @DisplayName("Doit rejeter la consultation d'une propriete avec un slug inexistant")
    void shouldRejectGetPropertyByNonExistentSlug() {
        when(propertyRepository.findBySlug("inexistant")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPropertyBySlug("inexistant"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Propriété");
    }

    // === Search Properties ===

    @Test
    @DisplayName("Doit rechercher les proprietes avec filtre de prix")
    void shouldSearchPropertiesWithPriceFilter() {
        var criteria = new PropertySearchCriteria(
                null, null, BigDecimal.valueOf(10), BigDecimal.valueOf(100),
                null, null, null, null, "newest", 0, 20
        );
        var property = createDomainProperty(propertyId, "Appartement", "appart-slug");
        var pageResult = new PageResult<>(List.of(property), 0, 20, 1L);

        when(propertyRepository.search(criteria)).thenReturn(pageResult);

        PageResult<PropertySummaryResponse> result = service.searchProperties(criteria);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Doit rechercher les proprietes avec filtre de type")
    void shouldSearchPropertiesWithTypeFilter() {
        var criteria = new PropertySearchCriteria(
                PropertyType.APARTMENT, null, null, null,
                null, null, null, null, "newest", 0, 20
        );
        var pageResult = new PageResult<Property>(List.of(), 0, 20, 0L);

        when(propertyRepository.search(criteria)).thenReturn(pageResult);

        PageResult<PropertySummaryResponse> result = service.searchProperties(criteria);

        assertThat(result.content()).isEmpty();
        verify(propertyRepository).search(criteria);
    }

    @Test
    @DisplayName("Doit rechercher les proprietes avec filtre de ville")
    void shouldSearchPropertiesWithCityFilter() {
        UUID cityId = UUID.randomUUID();
        var criteria = new PropertySearchCriteria(
                null, null, null, null,
                cityId, null, null, null, "newest", 0, 20
        );
        var pageResult = new PageResult<Property>(List.of(), 0, 20, 0L);

        when(propertyRepository.search(criteria)).thenReturn(pageResult);

        PageResult<PropertySummaryResponse> result = service.searchProperties(criteria);

        assertThat(result).isNotNull();
        verify(propertyRepository).search(criteria);
    }

    // === Publish / Unpublish ===

    @Test
    @DisplayName("Doit publier une propriete avec succes (DRAFT vers PUBLISHED)")
    void shouldPublishPropertySuccessfully() {
        PropertyEntity mockEntity = mock(PropertyEntity.class);
        UserEntity owner = new UserEntity();
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PhotoEntity photo = new PhotoEntity();
        List<PhotoEntity> photos = new ArrayList<>();
        photos.add(photo);

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getOwner()).thenReturn(owner);
        when(mockEntity.getPhotos()).thenReturn(photos);

        ApartmentEntity savedEntity = createMockApartmentEntity();
        savedEntity.setPublished(true);
        savedEntity.setStatus(PropertyStatus.PUBLISHED);
        when(propertyRepository.saveEntity(mockEntity)).thenReturn(savedEntity);

        PropertyResponse response = service.publishProperty(propertyId, ownerId);

        assertThat(response).isNotNull();
        verify(mockEntity).setPublished(true);
        verify(mockEntity).setStatus(PropertyStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Doit rejeter la publication d'une propriete sans photos")
    void shouldRejectPublishPropertyWithoutPhotos() {
        PropertyEntity mockEntity = mock(PropertyEntity.class);
        UserEntity owner = new UserEntity();
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getOwner()).thenReturn(owner);
        when(mockEntity.getPhotos()).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> service.publishProperty(propertyId, ownerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("photo");
    }

    @Test
    @DisplayName("Doit rejeter la publication par un non-proprietaire")
    void shouldRejectPublishByNonOwner() {
        PropertyEntity mockEntity = mock(PropertyEntity.class);
        UserEntity differentOwner = new UserEntity();
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(differentOwner, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getOwner()).thenReturn(differentOwner);

        assertThatThrownBy(() -> service.publishProperty(propertyId, ownerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("propriétaire");
    }

    @Test
    @DisplayName("Doit depublier une propriete avec succes (PUBLISHED vers DRAFT)")
    void shouldUnpublishPropertySuccessfully() {
        PropertyEntity mockEntity = mock(PropertyEntity.class);
        UserEntity owner = new UserEntity();
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getOwner()).thenReturn(owner);

        ApartmentEntity savedEntity = createMockApartmentEntity();
        savedEntity.setPublished(false);
        savedEntity.setStatus(PropertyStatus.DRAFT);
        when(propertyRepository.saveEntity(mockEntity)).thenReturn(savedEntity);

        PropertyResponse response = service.unpublishProperty(propertyId, ownerId);

        assertThat(response).isNotNull();
        verify(mockEntity).setPublished(false);
        verify(mockEntity).setStatus(PropertyStatus.DRAFT);
    }

    // === Get Properties By Owner ===

    @Test
    @DisplayName("Doit retourner les proprietes filtrees par proprietaire")
    void shouldReturnPropertiesByOwner() {
        var property = createDomainProperty(propertyId, "Mon Appartement", "mon-appart-slug");
        var pageResult = new PageResult<>(List.of(property), 0, 20, 1L);

        when(propertyRepository.findByOwnerId(ownerId, 0, 20)).thenReturn(pageResult);

        PageResult<PropertySummaryResponse> result = service.getPropertiesByOwner(ownerId, 0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(propertyRepository).findByOwnerId(ownerId, 0, 20);
    }

    // === Helpers ===

    private ApartmentEntity createMockApartmentEntity() {
        ApartmentEntity entity = new ApartmentEntity();
        try {
            var idField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, propertyId);
            var createdAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, Instant.now());
            var updatedAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(entity, Instant.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setTitle("Bel Appartement");
        entity.setDescription("Description");
        entity.setListingType(ListingType.RENT);
        entity.setPricePerDay(BigDecimal.valueOf(50));
        entity.setSlug("bel-appartement-abc12345");
        entity.setBedrooms(3);
        entity.setBathrooms(2);
        entity.setSquareMeters(80);
        entity.setStatus(PropertyStatus.DRAFT);

        UserEntity owner = new UserEntity();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setOwner(owner);

        AddressEntity address = new AddressEntity();
        try {
            var idField = AddressEntity.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(address, addressId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setAddress(address);
        entity.setPhotos(new ArrayList<>());

        return entity;
    }

    private CarEntity createMockCarEntity() {
        CarEntity entity = new CarEntity();
        try {
            var idField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, propertyId);
            var createdAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, Instant.now());
            var updatedAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(entity, Instant.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setTitle("Toyota Corolla");
        entity.setDescription("Belle voiture");
        entity.setListingType(ListingType.SHORT_TERM);
        entity.setPricePerDay(BigDecimal.valueOf(100));
        entity.setSlug("toyota-corolla-abc12345");
        entity.setBrand("Toyota");
        entity.setModel("Corolla");
        entity.setYear(2023);
        entity.setMileage(15000);
        entity.setFuelType(FuelType.GASOLINE);
        entity.setTransmission(Transmission.AUTOMATIC);
        entity.setStatus(PropertyStatus.DRAFT);

        UserEntity owner = new UserEntity();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setOwner(owner);

        AddressEntity address = new AddressEntity();
        try {
            var idField = AddressEntity.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(address, addressId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setAddress(address);
        entity.setPhotos(new ArrayList<>());

        return entity;
    }

    private LandEntity createMockLandEntity() {
        LandEntity entity = new LandEntity();
        try {
            var idField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, propertyId);
            var createdAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, Instant.now());
            var updatedAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(entity, Instant.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setTitle("Terrain Residenciel");
        entity.setDescription("Grand terrain");
        entity.setListingType(ListingType.SALE);
        entity.setPricePerDay(BigDecimal.valueOf(200));
        entity.setSalePrice(BigDecimal.valueOf(50000));
        entity.setSlug("terrain-residenciel-abc12345");
        entity.setLandType(LandType.RESIDENTIAL);
        entity.setSurfaceArea(1000);
        entity.setStatus(PropertyStatus.DRAFT);

        UserEntity owner = new UserEntity();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setOwner(owner);

        AddressEntity address = new AddressEntity();
        try {
            var idField = AddressEntity.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(address, addressId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entity.setAddress(address);
        entity.setPhotos(new ArrayList<>());

        return entity;
    }

    private Property createDomainProperty(UUID id, String title, String slug) {
        return new Property(
                id, title, "Description", PropertyType.APARTMENT, PropertyStatus.DRAFT,
                ListingType.RENT, BigDecimal.valueOf(50), BigDecimal.valueOf(500), null,
                slug, false, true, true, 0, null, 0,
                ownerId, "John Doe", null, null, addressId, companyId,
                3, 2, 80, null, null, null, null, null, null,
                null, null, null, null, List.of(), null,
                Instant.now(), Instant.now()
        );
    }
}
