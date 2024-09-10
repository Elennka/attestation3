package ru.inno.x_employees.model;

public record PatchEmployeeRequest(String lastName, String email,String url, String phone, boolean isActive) {
}
