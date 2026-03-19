package com.futela.api.domain.model.core;

import java.time.Instant;
import java.util.UUID;

public record PlatformSetting(
        UUID id,
        String key,
        String value,
        String category,
        String description,
        Instant updatedAt
) {
    public static final String KEY_VISIT_FEE = "visit_fee";
    public static final String KEY_DEFAULT_CURRENCY = "default_currency";
    public static final String KEY_RESERVATION_COMMISSION = "reservation_commission";

    public float floatValue() {
        return Float.parseFloat(value);
    }

    public int intValue() {
        return Integer.parseInt(value);
    }
}
