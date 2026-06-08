package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medical-histories")
@RequiredArgsConstructor
public class MedicalHistoryController {
    private final MedicalHistoryService service;
}
