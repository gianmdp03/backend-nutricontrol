package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanDetailDTO;
import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.NutritionalPlanService;
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
@RequestMapping("/api/nutritional-plans")
@RequiredArgsConstructor
public class NutritionalPlanController {
    private final NutritionalPlanService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<NutritionalPlanDetailDTO> createNutritionalPlan(
            @AuthenticationPrincipal User admin, @Valid @RequestBody NutritionalPlanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNutritionalPlan(admin, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    @GetMapping("/user")
    public ResponseEntity<Page<NutritionalPlanDetailDTO>> getAllUserNutritionalPlan(
            @AuthenticationPrincipal User user,
            @PageableDefault(page = 0, size = 18, sort = "dateTime", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllUserNutritionalPlans(user, pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<Page<NutritionalPlanDetailDTO>> adminGetUserNutritionalPlans(
            @PathVariable Long userId,
            @PageableDefault(page = 0, size = 18, sort = "dateTime", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.adminGetUserNutritionalPlans(userId, pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/manual")
    public ResponseEntity<NutritionalPlanDetailDTO> createManualNutritionalPlan(
            @AuthenticationPrincipal User admin, @Valid @RequestBody NutritionalPlanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createManualNutritionalPlan(admin, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/manual")
    public ResponseEntity<Page<NutritionalPlanDetailDTO>> getManualNutritionalPlans(
            @PageableDefault(page = 0, size = 18, sort = "dateTime", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.getManualNutritionalPlans(pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<byte[]> getPDFNutritionalPlan(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "plan-nutricional-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(service.getPDFNutritionalPlan(user, id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/manual/{id}")
    public ResponseEntity<byte[]> getManualPDFNutritionalPlan(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "plan-nutricional-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(service.getManualPDFNutritionalPlan(id));
    }
}
