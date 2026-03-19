package com.futela.api.application.dto.response.property;

import com.futela.api.domain.model.property.Photo;

import java.time.Instant;
import java.util.UUID;

public record PhotoResponse(
        UUID id,
        UUID propertyId,
        String url,
        String caption,
        int position,
        boolean isPrimary,
        Instant createdAt
) {
    public static PhotoResponse fromDomain(Photo photo) {
        return new PhotoResponse(
                photo.id(), photo.propertyId(), photo.url(),
                photo.caption(), photo.position(), photo.isPrimary(),
                photo.createdAt()
        );
    }
}
