package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.service.ScheduleExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule_exceptions")
@RequiredArgsConstructor
public class ScheduleExceptionController {
    private final ScheduleExceptionService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleExceptionDetailDTO> addScheduleException(@Valid @RequestBody ScheduleExceptionRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleException(dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ScheduleExceptionDetailDTO>> listScheduleExceptions(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleExceptions(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> getScheduleExceptionById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleExceptionById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> updateScheduleException(@PathVariable Long id, @Valid @RequestBody ScheduleExceptionUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleException(id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleException(@PathVariable Long id){
        service.deleteScheduleException(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
