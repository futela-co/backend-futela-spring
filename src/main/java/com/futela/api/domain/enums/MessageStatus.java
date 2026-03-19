package com.futela.api.domain.enums;

public enum MessageStatus {
    SENT("Envoyé", "blue"),
    DELIVERED("Délivré", "purple"),
    READ("Lu", "green");

    private final String label;
    private final String color;

    MessageStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
