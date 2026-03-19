package com.futela.api.presentation.controller.webhook;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.infrastructure.integration.flexpay.FlexPayWebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class FlexPayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(FlexPayWebhookController.class);

    private final FlexPayWebhookHandler webhookHandler;

    public FlexPayWebhookController(FlexPayWebhookHandler webhookHandler) {
        this.webhookHandler = webhookHandler;
    }

    @PostMapping("/flexpay")
    public ApiResponse<Map<String, Object>> handleWebhook(@RequestBody Map<String, Object> payload) {
        log.info("[Webhook] Received FlexPay callback");
        var result = webhookHandler.handle(payload);
        return ApiResponse.success(result);
    }
}
