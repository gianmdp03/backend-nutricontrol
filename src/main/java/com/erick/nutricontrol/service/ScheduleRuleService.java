package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRuleService {
    ScheduleRuleDetailDTO addScheduleRule(ScheduleRuleRequestDTO dto);
    Page<ScheduleRuleDetailDTO> listScheduleRules(Pageable pageable);
    ScheduleRuleDetailDTO getScheduleRuleById(Long id);
    ScheduleRuleDetailDTO updateScheduleRule(Long id, ScheduleRuleUpdateDTO dto);
    void deleteScheduleRuleById(Long id);
}
