package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateDetailDTO;
import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.MedicalCertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-certificates")
@RequiredArgsConstructor
public class MedicalCertificateController {
  private final MedicalCertificateService service;

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<MedicalCertificateDetailDTO> createMedicalCertificate(
      @AuthenticationPrincipal User admin, @Valid @RequestBody MedicalCertificateRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createMedicalCertificate(admin, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping("/user")
  public ResponseEntity<Page<MedicalCertificateDetailDTO>> getAllUserMedicalCertificate(
      @AuthenticationPrincipal User user, Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.getAllUserMedicalCertificates(user, pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/{id}")
  public ResponseEntity<Page<MedicalCertificateDetailDTO>> adminGetUserMedicalCertificate(
      @PathVariable Long userId, Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.adminGetUserMedicalCertificates(userId, pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping("/user/{id}")
  public ResponseEntity<byte[]> getPDFMedicalCertificate(
      @AuthenticationPrincipal User user, @PathVariable Long id) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("inline", "receta-" + id + ".pdf");
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(service.getPDFMedicalCertificate(user, id));
  }
}
