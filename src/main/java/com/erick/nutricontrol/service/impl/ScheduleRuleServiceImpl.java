package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.ScheduleRuleRepository;
import com.erick.nutricontrol.service.ScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ScheduleRuleServiceImpl implements ScheduleRuleService {
    private final ScheduleRuleRepository repository;
}
