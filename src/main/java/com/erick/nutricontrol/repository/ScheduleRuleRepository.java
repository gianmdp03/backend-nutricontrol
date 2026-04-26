package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRuleRepository extends JpaRepository<ScheduleRule, Long> {
}
