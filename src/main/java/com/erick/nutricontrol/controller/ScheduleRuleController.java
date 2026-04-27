package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.service.ScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule_rules")
@RequiredArgsConstructor
public class ScheduleRuleController {
    private final ScheduleRuleService service;

    @PostMapping
    public ResponseEntity<ScheduleRuleDetailDTO> addScheduleRule(@RequestBody ScheduleRuleRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleRule(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ScheduleRuleDetailDTO>> listScheduleRule(@PageableDefault(page = 0, size = 12, sort = "dayOfWeek", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleRules(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> getScheduleRuleById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleRuleById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleRuleDetailDTO> updateScheduleRule(@PathVariable Long id, @RequestBody ScheduleRuleUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleRule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleRule(@PathVariable Long id){
        service.deleteScheduleRuleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
