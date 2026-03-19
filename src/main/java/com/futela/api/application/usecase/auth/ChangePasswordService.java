package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.ChangePasswordRequest;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.in.auth.ChangePasswordUseCase;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));

        if (!passwordEncoder.matches(request.currentPassword(), user.passwordHash())) {
            throw new ValidationException("Le mot de passe actuel est incorrect");
        }

        String newPasswordHash = passwordEncoder.encode(request.newPassword());

        User updatedUser = new User(
                user.id(),
                user.email(),
                newPasswordHash,
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.avatar(),
                user.role(),
                user.status(),
                user.isVerified(),
                user.isAvailable(),
                user.profileCompleted(),
                user.emailVerifiedAt(),
                user.lastLoginAt(),
                user.companyId(),
                user.companyName(),
                user.createdAt(),
                user.updatedAt(),
                user.deletedAt()
        );

        userRepository.save(updatedUser);
    }
}
