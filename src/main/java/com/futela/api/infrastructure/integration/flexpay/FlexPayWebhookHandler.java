package com.futela.api.infrastructure.integration.flexpay;

import com.futela.api.domain.port.in.payment.ConfirmPaymentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlexPayWebhookHandler {

    private static final Logger log = LoggerFactory.getLogger(FlexPayWebhookHandler.class);

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    public FlexPayWebhookHandler(ConfirmPaymentUseCase confirmPaymentUseCase) {
        this.confirmPaymentUseCase = confirmPaymentUseCase;
    }

    public Map<String, Object> handle(Map<String, Object> payload) {
        log.info("[FlexPay Webhook] Received: {}", payload);

        String orderNumber = (String) payload.get("orderNumber");
        Object statusObj = payload.get("status");

        if (orderNumber == null || statusObj == null) {
            log.error("[FlexPay Webhook] Invalid payload");
            throw new IllegalArgumentException("Payload webhook invalide");
        }

        String status = mapFlexPayStatus(statusObj);

        var response = confirmPaymentUseCase.execute(orderNumber, status);

        log.info("[FlexPay Webhook] Processed: orderNumber={}, newStatus={}", orderNumber, response.status());

        return Map.of("success", true, "message", "Webhook traité avec succès");
    }

    private String mapFlexPayStatus(Object statusObj) {
        int code;
        try {
            code = Integer.parseInt(String.valueOf(statusObj));
        } catch (NumberFormatException e) {
            return String.valueOf(statusObj);
        }
        return switch (code) {
            case 0 -> "completed";
            case 1 -> "pending";
            case 2 -> "failed";
            case 3 -> "cancelled";
            default -> "pending";
        };
    }
}
