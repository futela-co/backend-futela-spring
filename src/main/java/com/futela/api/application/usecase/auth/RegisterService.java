package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.RegisterRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.model.core.Company;
import com.futela.api.domain.port.in.auth.RegisterUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.domain.port.out.common.CompanyRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepository;
    private final CompanyRepositoryPort companyRepository;
    private final DeviceSessionRepositoryPort deviceSessionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse execute(RegisterRequest request) {
        // 1. Vérifier qu'au moins un identifiant est fourni
        if ((request.email() == null || request.email().isBlank())
                && (request.phoneNumber() == null || request.phoneNumber().isBlank())) {
            throw new ValidationException("Un email ou un numéro de téléphone est requis");
        }

        // 2. Vérifier l'unicité
        if (request.email() != null && !request.email().isBlank() && userRepository.emailExists(request.email())) {
            throw new DuplicateResourceException("Un compte avec cet email existe déjà");
        }
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank() && userRepository.phoneExists(request.phoneNumber())) {
            throw new DuplicateResourceException("Un compte avec ce numéro de téléphone existe déjà");
        }

        // 3. Obtenir la company par défaut
        Company company = companyRepository.findOrCreateDefault();

        // 4. Déterminer l'email
        String email = (request.email() != null && !request.email().isBlank())
                ? request.email()
                : "phone_" + request.phoneNumber().replaceAll("[^0-9]", "") + "@futela.local";

        // 5. Créer l'utilisateur
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                null, email, hashedPassword,
                request.firstName(), request.lastName(),
                request.phoneNumber(), null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                company.id(), company.name(),
                Instant.now(), Instant.now(), null
        );
        User savedUser = userRepository.save(user);

        // 6. Créer la session d'appareil
        String fingerprint = request.deviceFingerprint() != null ? request.deviceFingerprint() : UUID.randomUUID().toString();
        DeviceSession session = new DeviceSession(
                null, savedUser.id(), request.deviceName(), fingerprint,
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );
        DeviceSession savedSession = deviceSessionRepository.save(session);

        // 7. Générer les tokens
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser, savedSession.id());
        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = jwtTokenProvider.hashRefreshToken(rawRefreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = new RefreshToken(
                null, savedSession.id(), tokenHash, expiresAt,
                null, false, Instant.now(), Instant.now()
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                rawRefreshToken,
                savedSession.id().toString(),
                (int) (jwtTokenProvider.getAccessTokenExpirationMs() / 1000),
                (int) (jwtTokenProvider.getRefreshTokenExpirationMs() / 1000),
                UserResponse.fromDomain(savedUser)
        );
    }
}
