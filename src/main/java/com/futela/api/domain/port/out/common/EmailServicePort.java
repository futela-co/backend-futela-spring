package com.futela.api.domain.port.out.common;

public interface EmailServicePort {

    void sendEmail(String to, String subject, String body);
}
