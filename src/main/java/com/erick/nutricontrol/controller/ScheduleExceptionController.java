package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule-exceptions")
@RequiredArgsConstructor
public class ScheduleExceptionController {
    private final ScheduleExceptionService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleExceptionDetailDTO> addScheduleException(Authentication authentication, @Valid @RequestBody ScheduleExceptionRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addScheduleException("zcepeda", dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ScheduleExceptionDetailDTO>> listScheduleExceptionByAdmin(Authentication authentication, @PageableDefault(page = 0, size = 12, sort = "date", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(service.listScheduleExceptionsByAdmin("zcepeda", pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> getScheduleExceptionById(Authentication authentication, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getScheduleExceptionById("zcepeda", id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleExceptionDetailDTO> updateScheduleException(Authentication authentication, @PathVariable Long id, @Valid @RequestBody ScheduleExceptionUpdateDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.updateScheduleException("zcepeda", id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleException(Authentication authentication, @PathVariable Long id){
        service.deleteScheduleException("zcepeda", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
