package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleRule;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleRuleRepository extends JpaRepository<ScheduleRule, Long> {
    List<ScheduleRule> findByDayOfWeek(DayOfWeek dayOfWeek);
    Page<ScheduleRule> findByAdmin(User admin, Pageable pageable);
    boolean existsByDayOfWeek(DayOfWeek dayOfWeek);
}
