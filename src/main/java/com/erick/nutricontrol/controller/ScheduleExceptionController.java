package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.ScheduleExceptionService;
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
@RequestMapping("/api/schedule-exceptions")
@RequiredArgsConstructor
public class ScheduleExceptionController {
    private final ScheduleExceptionService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleExceptionDetailDTO> addScheduleException(@AuthenticationPrincipal User user, @Valid @RequestBody ScheduleExceptionRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleException(user, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ScheduleExceptionDetailDTO>> listScheduleExceptionByAdmin(@AuthenticationPrincipal User user, @PageableDefault(page = 0, size = 12, sort = "date", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleExceptionsByAdmin(user, pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> getScheduleExceptionById(@AuthenticationPrincipal User user, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleExceptionById(user, id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> updateScheduleException(@AuthenticationPrincipal User user, @PathVariable Long id, @Valid @RequestBody ScheduleExceptionUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleException(user, id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleException(@AuthenticationPrincipal User user, @PathVariable Long id){
        service.deleteScheduleException(user, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
