package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentListDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    @PostMapping
    public ResponseEntity<AppointmentDetailDTO> addAppointment(Authentication authentication, @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addAppointment(authentication.getName(), dto));
    }

    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    @GetMapping("/available")
    public ResponseEntity<Map<LocalDate, List<LocalTime>>> getAvailableAppointments() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAvailableAppointments());
    }

    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    @GetMapping("/user")
    public ResponseEntity<Page<AppointmentListDTO>> listUserAppointments(Authentication authentication, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listUserAppointments(authentication.getName(), pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<AppointmentListDTO>> listAdminAppointments(Authentication authentication, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listAdminAppointments(authentication.getName(), pageable));
    }
}
