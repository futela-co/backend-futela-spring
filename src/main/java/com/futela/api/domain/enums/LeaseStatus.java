package com.futela.api.domain.enums;

public enum LeaseStatus {
    ACTIVE,
    TERMINATED,
    RENEWED,
    EXPIRED;

    public boolean canBeRenewed() {
        return this == ACTIVE || this == EXPIRED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
