package com.futela.api.infrastructure.integration.sms;

import com.futela.api.domain.port.out.common.SmsServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsServiceAdapter implements SmsServicePort {

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[SMS STUB] Envoi de SMS à {} | Message: {}", phoneNumber, message);
    }
}
