package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.UpdateProfileRequest;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.in.auth.UpdateProfileUseCase;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    public UserResponse execute(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));

        User updatedUser = new User(
                user.id(),
                user.email(),
                user.passwordHash(),
                request.firstName() != null ? request.firstName() : user.firstName(),
                request.lastName() != null ? request.lastName() : user.lastName(),
                request.phone() != null ? request.phone() : user.phone(),
                request.avatar() != null ? request.avatar() : user.avatar(),
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

        User saved = userRepository.save(updatedUser);
        return UserResponse.fromDomain(saved);
    }
}
