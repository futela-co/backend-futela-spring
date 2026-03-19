package com.futela.api.application.usecase.photo;

import com.futela.api.application.dto.response.property.PhotoResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Photo;
import com.futela.api.domain.port.out.common.FileStoragePort;
import com.futela.api.domain.port.out.property.PhotoRepositoryPort;
import com.futela.api.infrastructure.persistence.adapter.property.PropertyRepositoryAdapter;
import com.futela.api.infrastructure.persistence.entity.property.ApartmentEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoUseCaseServiceTest {

    @Mock
    private PhotoRepositoryPort photoRepository;

    @Mock
    private PropertyRepositoryAdapter propertyRepository;

    @Mock
    private FileStoragePort fileStorage;

    @InjectMocks
    private PhotoUseCaseService service;

    private UUID propertyId;
    private UUID photoId;
    private UUID ownerId;
    private PropertyEntity propertyEntity;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        photoId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        propertyEntity = mock(PropertyEntity.class);
        UserEntity owner = new UserEntity();
        try {
            var idField = UserEntity.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(owner, ownerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lenient().when(propertyEntity.getOwner()).thenReturn(owner);
    }

    @Test
    @DisplayName("Doit uploader une photo avec succes et position auto-incrementee")
    void shouldUploadPhotoSuccessfully() {
        InputStream inputStream = new ByteArrayInputStream("image-data".getBytes());
        String filename = "photo.jpg";
        String contentType = "image/jpeg";

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.countByPropertyId(propertyId)).thenReturn(3);
        when(fileStorage.upload(any(), any(), eq(filename), eq(contentType)))
                .thenReturn("https://cdn.example.com/photo.jpg");

        var savedPhoto = new Photo(photoId, propertyId, "https://cdn.example.com/photo.jpg",
                "Ma photo", 3, false, Instant.now());
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

        PhotoResponse response = service.uploadPhoto(propertyId, inputStream, filename,
                contentType, "Ma photo", ownerId);

        assertThat(response).isNotNull();
        assertThat(response.url()).isEqualTo("https://cdn.example.com/photo.jpg");
        assertThat(response.position()).isEqualTo(3);
        assertThat(response.isPrimary()).isFalse();

        ArgumentCaptor<Photo> captor = ArgumentCaptor.forClass(Photo.class);
        verify(photoRepository).save(captor.capture());
        assertThat(captor.getValue().position()).isEqualTo(3);
    }

    @Test
    @DisplayName("Doit definir la premiere photo comme photo principale")
    void shouldSetFirstPhotoAsPrimary() {
        InputStream inputStream = new ByteArrayInputStream("image-data".getBytes());

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.countByPropertyId(propertyId)).thenReturn(0);
        when(fileStorage.upload(any(), any(), any(), any())).thenReturn("https://cdn.example.com/photo.jpg");

        var savedPhoto = new Photo(photoId, propertyId, "https://cdn.example.com/photo.jpg",
                null, 0, true, Instant.now());
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

        service.uploadPhoto(propertyId, inputStream, "photo.jpg", "image/jpeg", null, ownerId);

        ArgumentCaptor<Photo> captor = ArgumentCaptor.forClass(Photo.class);
        verify(photoRepository).save(captor.capture());
        assertThat(captor.getValue().isPrimary()).isTrue();
    }

    @Test
    @DisplayName("Doit rejeter l'upload quand le maximum de 10 photos est atteint")
    void shouldRejectUploadWhenMaxPhotosReached() {
        InputStream inputStream = new ByteArrayInputStream("image-data".getBytes());

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.countByPropertyId(propertyId)).thenReturn(10);

        assertThatThrownBy(() -> service.uploadPhoto(propertyId, inputStream, "photo.jpg",
                "image/jpeg", null, ownerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("10 photos");

        verify(fileStorage, never()).upload(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Doit rejeter l'upload par un non-proprietaire")
    void shouldRejectUploadByNonOwner() {
        InputStream inputStream = new ByteArrayInputStream("image-data".getBytes());
        UUID differentOwnerId = UUID.randomUUID();

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));

        assertThatThrownBy(() -> service.uploadPhoto(propertyId, inputStream, "photo.jpg",
                "image/jpeg", null, differentOwnerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("propriétaire");
    }

    @Test
    @DisplayName("Doit supprimer une photo avec succes")
    void shouldDeletePhotoSuccessfully() {
        var photo = new Photo(photoId, propertyId, "https://cdn.example.com/photo.jpg",
                null, 0, false, Instant.now());

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        service.deletePhoto(propertyId, photoId, ownerId);

        verify(photoRepository).deleteById(photoId);
    }

    @Test
    @DisplayName("Doit rejeter la suppression d'une photo n'appartenant pas a la propriete")
    void shouldRejectDeletePhotoNotBelongingToProperty() {
        UUID otherPropertyId = UUID.randomUUID();
        var photo = new Photo(photoId, otherPropertyId, "https://cdn.example.com/photo.jpg",
                null, 0, false, Instant.now());

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        assertThatThrownBy(() -> service.deletePhoto(propertyId, photoId, ownerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("n'appartient pas");
    }

    @Test
    @DisplayName("Doit definir une photo comme principale et retirer l'ancienne")
    void shouldSetPrimaryPhotoAndClearOld() {
        var photo = new Photo(photoId, propertyId, "https://cdn.example.com/photo.jpg",
                "Caption", 2, false, Instant.now());

        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        var updatedPhoto = new Photo(photoId, propertyId, "https://cdn.example.com/photo.jpg",
                "Caption", 2, true, Instant.now());
        when(photoRepository.save(any(Photo.class))).thenReturn(updatedPhoto);

        PhotoResponse response = service.setPrimaryPhoto(propertyId, photoId, ownerId);

        assertThat(response).isNotNull();
        assertThat(response.isPrimary()).isTrue();
        verify(photoRepository).unsetPrimaryByPropertyId(propertyId);
        verify(photoRepository).save(any(Photo.class));
    }

    @Test
    @DisplayName("Doit rejeter la definition de photo principale pour une photo inexistante")
    void shouldRejectSetPrimaryForNonExistentPhoto() {
        when(propertyRepository.findEntityById(propertyId)).thenReturn(Optional.of(propertyEntity));
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setPrimaryPhoto(propertyId, photoId, ownerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo");
    }
}
