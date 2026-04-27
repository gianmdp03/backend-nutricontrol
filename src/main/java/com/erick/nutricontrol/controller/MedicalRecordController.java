package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical_records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService service;
}
