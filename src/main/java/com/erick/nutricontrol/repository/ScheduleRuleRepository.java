package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleRuleRepository extends JpaRepository<ScheduleRule, Long> {
    List<ScheduleRule> findByDayOfWeek(DayOfWeek dayOfWeek);
}
