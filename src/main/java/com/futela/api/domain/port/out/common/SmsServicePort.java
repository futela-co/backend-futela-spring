package com.futela.api.domain.port.out.common;

public interface SmsServicePort {

    void sendSms(String phoneNumber, String message);
}
