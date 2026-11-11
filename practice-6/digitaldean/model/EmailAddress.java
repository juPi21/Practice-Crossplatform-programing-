package ua.edu.digitaldean.model;

import java.util.Locale;

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Електронна адреса не може бути порожньою.");
        }
        value = value.strip().toLowerCase(Locale.ROOT);
        if (value.contains(" ") || !value.contains("@") || value.indexOf('@') != value.lastIndexOf('@') || value.startsWith("@") || value.endsWith("@")) {
            throw new IllegalArgumentException("Електронна адреса має містити один символ @ між локальною та доменною частинами без пробілів.");
        }
    }
}