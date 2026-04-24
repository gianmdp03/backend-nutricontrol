package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
}
