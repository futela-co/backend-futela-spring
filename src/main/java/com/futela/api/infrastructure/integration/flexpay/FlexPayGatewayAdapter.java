package com.futela.api.infrastructure.integration.flexpay;

import com.futela.api.domain.port.out.common.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FlexPayGatewayAdapter implements PaymentGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(FlexPayGatewayAdapter.class);

    private final FlexPayConfig config;
    private final RestTemplate restTemplate;

    public FlexPayGatewayAdapter(FlexPayConfig config, RestTemplateBuilder builder) {
        this.config = config;
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public Map<String, Object> initiatePayment(BigDecimal amount, String currency, String phone, String reference) {
        log.info("[FlexPay] Initiating payment: amount={}, currency={}, phone={}, ref={}", amount, currency, phone, reference);

        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            log.warn("[FlexPay] API key not configured");
            return Map.of("success", false, "externalId", "", "orderNumber", "", "message", "FlexPay API key not configured");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());

            Map<String, String> payload = Map.of(
                    "merchant", config.getMerchant(),
                    "type", "1",
                    "phone", phone,
                    "reference", reference,
                    "amount", amount.toPlainString(),
                    "currency", currency,
                    "callbackUrl", config.getCallbackUrl() != null ? config.getCallbackUrl() : ""
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            String url = config.getBaseUrl() + "/paymentService";

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && "0".equals(String.valueOf(response.get("code")))) {
                String orderNumber = (String) response.get("orderNumber");
                return Map.of(
                        "success", true,
                        "externalId", orderNumber != null ? orderNumber : "",
                        "orderNumber", orderNumber != null ? orderNumber : "",
                        "message", response.getOrDefault("message", "Paiement initié avec succès")
                );
            }

            return Map.of(
                    "success", false,
                    "externalId", "",
                    "orderNumber", "",
                    "message", response != null ? response.getOrDefault("message", "Échec FlexPay") : "Réponse vide"
            );
        } catch (Exception e) {
            log.error("[FlexPay] Initiation error: {}", e.getMessage());
            return Map.of("success", false, "externalId", "", "orderNumber", "", "message", "Erreur FlexPay: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String externalId) {
        log.info("[FlexPay] Verifying payment: {}", externalId);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(config.getApiKey());

            HttpEntity<Void> request = new HttpEntity<>(headers);
            String url = config.getBaseUrl() + "/check/" + externalId;

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

            Map<String, Object> body = response.getBody();
            if (body != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tx = (Map<String, Object>) body.get("transaction");
                if (tx != null && ("0".equals(String.valueOf(tx.get("status"))) || Integer.valueOf(0).equals(tx.get("status")))) {
                    return Map.of("status", "completed", "externalId", externalId);
                }
            }

            return Map.of("status", "pending", "externalId", externalId);
        } catch (Exception e) {
            log.error("[FlexPay] Verification error: {}", e.getMessage());
            return Map.of("status", "pending", "externalId", externalId);
        }
    }

    @Override
    public String generateOrderNumber() {
        return "FPY_" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
