package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordDetailDTO;
import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
  private final MedicalRecordService service;

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @PostMapping
  public ResponseEntity<MedicalRecordDetailDTO> saveOrUpdateMedicalRecord(
      @AuthenticationPrincipal User user, @Valid @RequestBody MedicalRecordRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.saveOrUpdateMedicalRecord(user, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping
  public ResponseEntity<MedicalRecordDetailDTO> getUserMedicalRecord(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getUserMedicalRecord(user));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/{patientId}")
  public ResponseEntity<MedicalRecordDetailDTO> getPatientMedicalRecord(
      @PathVariable Long patientId) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getPatientMedicalRecord(patientId));
  }
}
