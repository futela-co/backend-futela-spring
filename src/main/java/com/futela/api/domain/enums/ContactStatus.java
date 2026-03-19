package com.futela.api.domain.enums;

public enum ContactStatus {
    NEW("Nouveau", "blue"),
    IN_PROGRESS("En cours", "yellow"),
    RESPONDED("Répondu", "green"),
    CLOSED("Fermé", "gray");

    private final String label;
    private final String color;

    ContactStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
