package com.futela.api.infrastructure.persistence.adapter.property;

import com.futela.api.domain.model.property.Photo;
import com.futela.api.domain.port.out.property.PhotoRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.property.PhotoEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.mapper.property.PhotoPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.property.JpaPhotoRepository;
import com.futela.api.infrastructure.persistence.repository.property.JpaPropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PhotoRepositoryAdapter implements PhotoRepositoryPort {

    private final JpaPhotoRepository jpaRepository;
    private final JpaPropertyRepository propertyRepository;

    public PhotoRepositoryAdapter(JpaPhotoRepository jpaRepository, JpaPropertyRepository propertyRepository) {
        this.jpaRepository = jpaRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Photo save(Photo photo) {
        PhotoEntity entity;
        if (photo.id() != null) {
            entity = jpaRepository.findById(photo.id()).orElse(new PhotoEntity());
        } else {
            entity = new PhotoEntity();
        }
        PropertyEntity property = propertyRepository.getReferenceById(photo.propertyId());
        entity.setProperty(property);
        entity.setUrl(photo.url());
        entity.setCaption(photo.caption());
        entity.setDisplayOrder(photo.position());
        entity.setPrimary(photo.isPrimary());
        return PhotoPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Photo> findById(UUID id) {
        return jpaRepository.findById(id).map(PhotoPersistenceMapper::toDomain);
    }

    @Override
    public List<Photo> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyIdOrderByDisplayOrderAsc(propertyId).stream()
                .map(PhotoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int countByPropertyId(UUID propertyId) {
        return jpaRepository.countByPropertyId(propertyId);
    }

    @Override
    @Transactional
    public void unsetPrimaryByPropertyId(UUID propertyId) {
        jpaRepository.unsetPrimaryByPropertyId(propertyId);
    }

    @Override
    public void saveAll(List<Photo> photos) {
        for (Photo photo : photos) {
            save(photo);
        }
    }
}
