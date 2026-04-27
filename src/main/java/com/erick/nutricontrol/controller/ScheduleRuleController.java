package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule_rules")
@RequiredArgsConstructor
public class ScheduleRuleController {
    private final ScheduleRuleService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleRuleDetailDTO> addScheduleRule(@Valid @RequestBody ScheduleRuleRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleRule(dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ScheduleRuleDetailDTO>> listScheduleRule(@PageableDefault(page = 0, size = 12, sort = "dayOfWeek", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleRules(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> getScheduleRuleById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleRuleById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> updateScheduleRule(@PathVariable Long id, @Valid @RequestBody ScheduleRuleUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleRule(id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleRule(@PathVariable Long id){
        service.deleteScheduleRuleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
