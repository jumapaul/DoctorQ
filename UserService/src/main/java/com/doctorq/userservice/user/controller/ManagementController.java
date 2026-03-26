package com.doctorq.userservice.user.controller;

import com.doctorq.userservice.kafka.AssignToDoctorDto;
import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user.service.ManagementServiceImpl;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/manage")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementServiceImpl managementService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("admin/{userId}")
    public ResponseEntity<UserResponseDto> assignAdminRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(managementService.changeUserRole(userId, Roles.ADMIN));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> assignUserRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(managementService.changeUserRole(userId, Roles.USER));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/doctor")
    public ResponseEntity<UserResponseDto> assignDoctorRole(
            @RequestBody AssignToDoctorDto toDoctorDto
    ) {
        return ResponseEntity.ok(managementService.changeRoleToDoctor(toDoctorDto));
    }
}
