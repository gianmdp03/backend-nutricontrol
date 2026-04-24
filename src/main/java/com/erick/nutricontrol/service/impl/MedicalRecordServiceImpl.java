package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.MedicalRecordRepository;
import com.erick.nutricontrol.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository repository;
}
