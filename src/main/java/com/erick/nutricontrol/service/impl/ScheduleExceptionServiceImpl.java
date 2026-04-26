package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
import com.erick.nutricontrol.service.ScheduleExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ScheduleExceptionServiceImpl implements ScheduleExceptionService {
    private final ScheduleExceptionRepository repository;
}
