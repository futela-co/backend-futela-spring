package com.futela.api.infrastructure.storage;

import com.futela.api.domain.port.out.common.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class CloudinaryStorageAdapter implements FileStoragePort {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Override
    public String upload(InputStream inputStream, String folder, String filename, String contentType) {
        try {
            var cloudinary = new com.cloudinary.Cloudinary(Map.of(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));

            byte[] bytes = inputStream.readAllBytes();
            var result = cloudinary.uploader().upload(bytes, Map.of(
                    "folder", folder,
                    "public_id", filename.replaceAll("\\.[^.]+$", ""),
                    "resource_type", "image"
            ));

            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier vers Cloudinary", e);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            var cloudinary = new com.cloudinary.Cloudinary(Map.of(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier Cloudinary", e);
        }
    }
}
