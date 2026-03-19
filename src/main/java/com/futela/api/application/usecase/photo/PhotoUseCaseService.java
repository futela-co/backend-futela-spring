package com.futela.api.application.usecase.photo;

import com.futela.api.application.dto.response.property.PhotoResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Photo;
import com.futela.api.domain.port.out.common.FileStoragePort;
import com.futela.api.domain.port.out.property.PhotoRepositoryPort;
import com.futela.api.infrastructure.persistence.adapter.property.PropertyRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PhotoUseCaseService {

    private final PhotoRepositoryPort photoRepository;
    private final PropertyRepositoryAdapter propertyRepository;
    private final FileStoragePort fileStorage;

    public PhotoUseCaseService(PhotoRepositoryPort photoRepository,
                               PropertyRepositoryAdapter propertyRepository,
                               FileStoragePort fileStorage) {
        this.photoRepository = photoRepository;
        this.propertyRepository = propertyRepository;
        this.fileStorage = fileStorage;
    }

    public PhotoResponse uploadPhoto(UUID propertyId, InputStream inputStream, String filename,
                                      String contentType, String caption, UUID ownerId) {
        var property = propertyRepository.findEntityById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", propertyId.toString()));

        if (!property.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        int photoCount = photoRepository.countByPropertyId(propertyId);
        if (photoCount >= 10) {
            throw new InvalidOperationException("Maximum 10 photos par propriété");
        }

        String folder = "futela/properties/" + propertyId;
        String url = fileStorage.upload(inputStream, folder, filename, contentType);

        boolean isPrimary = photoCount == 0;
        var photo = new Photo(null, propertyId, url, caption, photoCount, isPrimary, null);
        return PhotoResponse.fromDomain(photoRepository.save(photo));
    }

    public void deletePhoto(UUID propertyId, UUID photoId, UUID ownerId) {
        var property = propertyRepository.findEntityById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", propertyId.toString()));

        if (!property.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        var photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo", photoId.toString()));

        if (!photo.propertyId().equals(propertyId)) {
            throw new InvalidOperationException("Cette photo n'appartient pas à cette propriété");
        }

        photoRepository.deleteById(photoId);
    }

    public PhotoResponse setPrimaryPhoto(UUID propertyId, UUID photoId, UUID ownerId) {
        var property = propertyRepository.findEntityById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", propertyId.toString()));

        if (!property.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        var photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo", photoId.toString()));

        if (!photo.propertyId().equals(propertyId)) {
            throw new InvalidOperationException("Cette photo n'appartient pas à cette propriété");
        }

        photoRepository.unsetPrimaryByPropertyId(propertyId);
        var updated = new Photo(photo.id(), photo.propertyId(), photo.url(),
                photo.caption(), photo.position(), true, photo.createdAt());
        return PhotoResponse.fromDomain(photoRepository.save(updated));
    }

    public List<PhotoResponse> reorderPhotos(UUID propertyId, List<UUID> photoIds, UUID ownerId) {
        var property = propertyRepository.findEntityById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", propertyId.toString()));

        if (!property.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        var existingPhotos = photoRepository.findByPropertyId(propertyId);
        List<Photo> updatedPhotos = new ArrayList<>();

        for (int i = 0; i < photoIds.size(); i++) {
            UUID photoId = photoIds.get(i);
            int position = i;
            existingPhotos.stream()
                    .filter(p -> p.id().equals(photoId))
                    .findFirst()
                    .ifPresent(p -> updatedPhotos.add(
                            new Photo(p.id(), p.propertyId(), p.url(), p.caption(),
                                    position, p.isPrimary(), p.createdAt())
                    ));
        }

        photoRepository.saveAll(updatedPhotos);
        return photoRepository.findByPropertyId(propertyId).stream()
                .map(PhotoResponse::fromDomain)
                .toList();
    }
}
