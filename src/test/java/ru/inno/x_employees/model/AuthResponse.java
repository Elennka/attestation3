package ru.inno.x_employees.model;

public record AuthResponse(String userToken, String role, String displayName, String login) {
}
