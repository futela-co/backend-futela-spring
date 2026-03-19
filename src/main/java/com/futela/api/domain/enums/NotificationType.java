package com.futela.api.domain.enums;

public enum NotificationType {
    RESERVATION("Réservation", "blue"),
    PAYMENT("Paiement", "green"),
    MESSAGE("Message", "purple"),
    REVIEW("Avis", "yellow"),
    RENT("Loyer", "orange"),
    SYSTEM("Système", "gray");

    private final String label;
    private final String color;

    NotificationType(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
