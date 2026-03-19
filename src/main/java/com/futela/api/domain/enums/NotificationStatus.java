package com.futela.api.domain.enums;

public enum NotificationStatus {
    UNREAD("Non lu", "blue"),
    READ("Lu", "gray"),
    ARCHIVED("Archivé", "yellow");

    private final String label;
    private final String color;

    NotificationStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
