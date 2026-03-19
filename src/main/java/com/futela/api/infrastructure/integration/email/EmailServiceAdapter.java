package com.futela.api.infrastructure.integration.email;

import com.futela.api.domain.port.out.common.EmailServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailServiceAdapter implements EmailServicePort {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[EMAIL STUB] Envoi d'email à {} | Sujet: {} | Corps: {}", to, subject, body);
    }
}
