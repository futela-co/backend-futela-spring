package com.futela.api.domain.enums;

public enum NotificationChannel {
    EMAIL("Email", "blue"),
    SMS("SMS", "green"),
    PUSH("Push", "purple"),
    IN_APP("In-App", "gray");

    private final String label;
    private final String color;

    NotificationChannel(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
