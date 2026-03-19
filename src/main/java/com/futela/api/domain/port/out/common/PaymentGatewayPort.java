package com.futela.api.domain.port.out.common;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayPort {

    /**
     * Initiate a payment via FlexPay.
     *
     * @return a map with keys: success, externalId, orderNumber, message
     */
    Map<String, Object> initiatePayment(
            BigDecimal amount,
            String currency,
            String phone,
            String reference
    );

    /**
     * Verify a payment status with FlexPay.
     *
     * @return a map with keys: status, externalId, amount, currency, reference
     */
    Map<String, Object> verifyPayment(String externalId);

    /**
     * Generate a unique order number.
     */
    String generateOrderNumber();
}
