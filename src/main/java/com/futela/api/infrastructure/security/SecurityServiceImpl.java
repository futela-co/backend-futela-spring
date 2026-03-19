package com.futela.api.infrastructure.security;

import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UUID getCurrentUserId() {
        String token = getTokenFromContext();
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public UUID getCurrentCompanyId() {
        String token = getTokenFromContext();
        return jwtTokenProvider.getCompanyIdFromToken(token);
    }

    @Override
    public UUID getCurrentSessionId() {
        String token = getTokenFromContext();
        return jwtTokenProvider.getSessionIdFromToken(token);
    }

    private String getTokenFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }
        Object credentials = authentication.getCredentials();
        if (credentials instanceof String token) {
            return token;
        }
        throw new UnauthorizedException("Jeton d'authentification invalide");
    }
}
