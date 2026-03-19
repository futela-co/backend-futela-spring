package com.futela.api.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}
