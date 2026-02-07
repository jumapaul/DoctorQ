package com.doctorq.doctorservice.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Roles {
    ADMIN(
            Set.of(
                    Permissions.ADMIN_READ,
                    Permissions.ADMIN_CREATE,
                    Permissions.ADMIN_DELETE,
                    Permissions.ADMIN_UPDATE,
                    Permissions.USER_READ,
                    Permissions.DOCTOR_READ
            )
    ),
    DOCTOR(
            Set.of(
                    Permissions.DOCTOR_CREATE,
                    Permissions.DOCTOR_DELETE,
                    Permissions.DOCTOR_UPDATE,
                    Permissions.DOCTOR_READ
            )
    ),
    USER(
            Set.of(
                    Permissions.USER_CREATE,
                    Permissions.USER_DELETE,
                    Permissions.USER_UPDATE,
                    Permissions.USER_READ
            )
    );

    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permissions -> new SimpleGrantedAuthority(permissions.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}