package com.library.model;

public enum UserType {
    STUDENT("Öğrenci"),
    ACADEMICIAN("Akademisyen"),
    ADMIN("Yönetici");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
