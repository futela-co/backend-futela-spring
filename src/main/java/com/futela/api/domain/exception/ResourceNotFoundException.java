package com.futela.api.domain.exception;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("RESOURCE_NOT_FOUND", resourceType + " non trouvé(e) avec l'identifiant : " + identifier);
    }
}
