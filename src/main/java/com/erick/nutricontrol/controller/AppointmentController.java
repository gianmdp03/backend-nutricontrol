package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.dto.appointment.AvailableSlotDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.AppointmentService;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
  private final AppointmentService service;

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @PostMapping
  public ResponseEntity<PaymentOrderResponseDTO> addAppointment(
      @AuthenticationPrincipal User user, @Valid @RequestBody AppointmentRequestDTO dto)
      throws IOException, ApiException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.addAppointment(user, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping("/available")
  public ResponseEntity<List<AvailableSlotDTO>> getAvailableAppointments() {
    return ResponseEntity.status(HttpStatus.OK).body(service.getAvailableAppointments());
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping("/user")
  public ResponseEntity<Page<AppointmentDetailDTO>> listUserAppointments(
      @AuthenticationPrincipal User user,
      @PageableDefault(page = 0, size = 24, sort = "startTimeUtc", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(service.listUserAppointments(user, pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin")
  public ResponseEntity<Page<AppointmentDetailDTO>> listAdminAppointments(
      @AuthenticationPrincipal User user,
      @PageableDefault(page = 0, size = 24, sort = "startTimeUtc", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(service.listAdminAppointments(user, pageable));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PATIENT')")
  @GetMapping("/by-id/{id}")
  public ResponseEntity<AppointmentDetailDTO> getAppointmentById(
      @AuthenticationPrincipal User user, @PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getAppointmentById(user, id));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAppointment(
      @PathVariable Long id, @AuthenticationPrincipal User user) {
    service.deleteAppointment(id, user);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping("/admin/{id}")
  public ResponseEntity<Void> adminDeleteAppointment(
      @PathVariable Long id, @RequestParam(defaultValue = "true") boolean refund) {
    service.adminDeleteAppointment(id, refund);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
