package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PhotoResponse;

import java.io.InputStream;
import java.util.UUID;

public interface UploadPropertyPhotoUseCase {
    PhotoResponse execute(UUID propertyId, InputStream inputStream, String filename, String contentType, String caption, UUID ownerId);
}
