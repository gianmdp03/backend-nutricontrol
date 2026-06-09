package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryUpdateDTO;
import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.MedicalHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medical-histories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class MedicalHistoryController {
  private final MedicalHistoryService service;

  @PostMapping("/first")
  public ResponseEntity<MedicalHistoryDetailDTO> createMedicalHistoryAndFirstTracking(
      @AuthenticationPrincipal User admin, @Valid @RequestBody MedicalHistoryRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createMedicalHistoryAndFirstTracking(admin, dto));
  }

  @PostMapping("/{userId}")
  public ResponseEntity<MedicalHistoryDetailDTO> addMedicalHistoryTrackingToExistingHistory(
      @PathVariable Long userId,
      @AuthenticationPrincipal User admin,
      @Valid @RequestBody MedicalHistoryTrackingRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.addMedicalHistoryTrackingToExistingHistory(userId, admin, dto));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<MedicalHistoryDetailDTO> getUserMedicalHistory(
      @PathVariable Long userId, @AuthenticationPrincipal User admin) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getUserMedicalHistory(userId, admin));
  }

  @GetMapping("/exists/{userId}")
  public ResponseEntity<Boolean> checkMedicalHistory(@PathVariable Long userId) {
    return ResponseEntity.status(HttpStatus.OK).body(service.checkMedicalHistory(userId));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<MedicalHistoryDetailDTO> updateMedicalHistory(
      @PathVariable Long userId,
      @AuthenticationPrincipal User admin,
      @Valid @RequestBody MedicalHistoryUpdateDTO dto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.updateMedicalHistory(userId, admin, dto));
  }

  @GetMapping("/pdf/{userId}")
  public ResponseEntity<byte[]> getPDFMedicalHistory(
      @PathVariable Long userId, @RequestParam(required = false) Long trackingId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("inline", "historia-medica" + ".pdf");
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(service.getPDFMedicalHistory(userId, trackingId));
  }
}
