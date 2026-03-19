package com.futela.api.domain.enums;

public enum MessageType {
    TEXT("Message texte", "blue"),
    IMAGE("Image", "purple"),
    FILE("Fichier", "gray");

    private final String label;
    private final String color;

    MessageType(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() { return label; }
    public String color() { return color; }
}
