package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRuleService {
    ScheduleRuleDetailDTO addScheduleRule(String username, ScheduleRuleRequestDTO dto);
    Page<ScheduleRuleDetailDTO> listScheduleRulesByAdmin(String username, Pageable pageable);
    ScheduleRuleDetailDTO getScheduleRuleById(String username, Long id);
    ScheduleRuleDetailDTO updateScheduleRule(String username, Long id, ScheduleRuleUpdateDTO dto);
    void deleteScheduleRuleById(String username, Long id);
}
