package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.service.ServiceDetailDTO;
import com.erick.nutricontrol.dto.service.ServiceRequestDTO;
import com.erick.nutricontrol.dto.service.ServiceUpdateDTO;
import com.erick.nutricontrol.service.ServiceService;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ServiceDetailDTO> addService(@Valid @RequestBody ServiceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addService(dto));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<ServiceDetailDTO>> listServices(@PageableDefault(page = 0, size = 12, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listServices(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ServiceDetailDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getServiceById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ServiceDetailDTO> updateService(@PathVariable Long id, @Valid @RequestBody ServiceUpdateDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateService(id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        service.deleteService(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
