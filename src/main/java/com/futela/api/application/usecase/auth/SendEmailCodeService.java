package com.futela.api.application.usecase.auth;

import com.futela.api.domain.port.in.auth.SendEmailCodeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SendEmailCodeService implements SendEmailCodeUseCase {

    @Override
    public void execute(UUID userId) {
        // TODO: Implémenter l'envoi du code de vérification email
        log.info("Envoi du code de vérification email pour l'utilisateur {}", userId);
        throw new UnsupportedOperationException("L'envoi du code email n'est pas encore implémenté");
    }
}
