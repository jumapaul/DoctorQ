package com.doctorq.appointmentservice.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permissions {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete"),

    DOCTOR_READ("doctor:read"),
    DOCTOR_UPDATE("doctor:update"),
    DOCTOR_CREATE("doctor:create"),
    DOCTOR_DELETE("doctor:delete");

    private final String permission;
}
