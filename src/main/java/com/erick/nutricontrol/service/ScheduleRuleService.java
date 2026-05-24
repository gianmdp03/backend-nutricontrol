package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleRuleService {
    ScheduleRuleDetailDTO addScheduleRule(User user, ScheduleRuleRequestDTO dto);
    Page<ScheduleRuleDetailDTO> listScheduleRulesByAdmin(User user, Pageable pageable);
    ScheduleRuleDetailDTO getScheduleRuleById(User user, Long id);
    ScheduleRuleDetailDTO updateScheduleRule(User user, Long id, ScheduleRuleUpdateDTO dto);
    void deleteScheduleRuleById(User user, Long id);
}
