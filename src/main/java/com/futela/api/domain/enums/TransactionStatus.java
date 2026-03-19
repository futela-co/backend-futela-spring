package com.futela.api.domain.enums;

public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED;

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
}
