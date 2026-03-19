package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.in.auth.GetCurrentUserUseCase;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    public UserResponse execute(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));
        return UserResponse.fromDomain(user);
    }
}
