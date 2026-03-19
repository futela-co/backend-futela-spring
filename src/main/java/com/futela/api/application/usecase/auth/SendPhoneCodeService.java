package com.futela.api.application.usecase.auth;

import com.futela.api.domain.port.in.auth.SendPhoneCodeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SendPhoneCodeService implements SendPhoneCodeUseCase {

    @Override
    public void execute(UUID userId) {
        // TODO: Implémenter l'envoi du code de vérification par SMS
        log.info("Envoi du code de vérification SMS pour l'utilisateur {}", userId);
        throw new UnsupportedOperationException("L'envoi du code SMS n'est pas encore implémenté");
    }
}
