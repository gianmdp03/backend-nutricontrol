package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.ScheduleRuleService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/schedule-rules")
@RequiredArgsConstructor
public class ScheduleRuleController {
    private final ScheduleRuleService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleRuleDetailDTO> addScheduleRule(@AuthenticationPrincipal User user, @Valid @RequestBody ScheduleRuleRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleRule(user, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ScheduleRuleDetailDTO>> listScheduleRulesByAdmin(@AuthenticationPrincipal User user, @PageableDefault(page = 0, size = 12, sort = "dayOfWeek", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleRulesByAdmin(user, pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> getScheduleRuleById(@AuthenticationPrincipal User user, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleRuleById(user, id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> updateScheduleRule(@AuthenticationPrincipal User user, @PathVariable Long id, @Valid @RequestBody ScheduleRuleUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleRule(user, id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleRule(@AuthenticationPrincipal User user, @PathVariable Long id){
        service.deleteScheduleRuleById(user, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
