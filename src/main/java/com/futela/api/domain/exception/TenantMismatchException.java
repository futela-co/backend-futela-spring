package com.futela.api.domain.exception;

public class TenantMismatchException extends DomainException {
    public TenantMismatchException() {
        super("TENANT_MISMATCH", "Accès interdit : cette ressource n'appartient pas à votre organisation");
    }
}
