package com.futela.api.application.usecase.auth;

import com.futela.api.domain.port.in.auth.ConfirmEmailUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ConfirmEmailService implements ConfirmEmailUseCase {

    @Override
    public void execute(UUID userId, String code) {
        // TODO: Implémenter la vérification du code email
        log.info("Confirmation email pour l'utilisateur {} avec le code {}", userId, code);
        throw new UnsupportedOperationException("La confirmation email n'est pas encore implémentée");
    }
}
