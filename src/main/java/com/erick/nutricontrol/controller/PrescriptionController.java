package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.prescription.PrescriptionDetailDTO;
import com.erick.nutricontrol.dto.prescription.PrescriptionRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
  private final PrescriptionService service;

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<PrescriptionDetailDTO> createPrescription(
      @AuthenticationPrincipal User admin, @Valid @RequestBody PrescriptionRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createPrescription(admin, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @GetMapping("/user")
  public ResponseEntity<Page<PrescriptionDetailDTO>> getAllUserPrescriptions(
      @AuthenticationPrincipal User user,
      @PageableDefault(page = 0, size = 24, sort = "dateTime", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.getAllUserPrescriptions(user, pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/{id}")
  public ResponseEntity<Page<PrescriptionDetailDTO>> adminGetUserPrescriptions(
      @PathVariable Long userId,
      @PageableDefault(page = 0, size = 24, sort = "dateTime", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.adminGetUserPrescriptions(userId, pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping("/admin/manual")
  public ResponseEntity<PrescriptionDetailDTO> createManualPrescription(
      @AuthenticationPrincipal User admin, @Valid @RequestBody PrescriptionRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createManualPrescription(admin, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/manual")
  public ResponseEntity<Page<PrescriptionDetailDTO>> getManualPrescriptions(
      @PageableDefault(page = 0, size = 18, sort = "dateTime", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.getManualPrescriptions(pageable));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_ADMIN')")
  @GetMapping("/user/{id}")
  public ResponseEntity<byte[]> getPDFPrescription(
      @AuthenticationPrincipal User user, @PathVariable Long id) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("inline", "receta-" + id + ".pdf");
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(service.getPDFPrescription(user, id));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/manual/{id}")
  public ResponseEntity<byte[]> getManualPDFPrescription(@PathVariable Long id) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("inline", "receta-" + id + ".pdf");
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(service.getManualPDFPrescription(id));
  }
}
