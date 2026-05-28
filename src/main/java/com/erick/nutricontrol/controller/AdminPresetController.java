package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.adminPreset.AdminPresetDetailDTO;
import com.erick.nutricontrol.dto.adminPreset.AdminPresetRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.AdminPresetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-preset")
@RequiredArgsConstructor
public class AdminPresetController {
  private final AdminPresetService service;

  @PostMapping
  public ResponseEntity<AdminPresetDetailDTO> createOrUpdateAdminPreset(
      @AuthenticationPrincipal User admin, @Valid @RequestBody AdminPresetRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createOrUpdateAdminPreset(admin, dto));
  }

  @GetMapping
  public ResponseEntity<AdminPresetDetailDTO> getAdminPreset(@AuthenticationPrincipal User admin) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getAdminPreset(admin));
  }
}
