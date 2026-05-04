package com.erick.nutricontrol.dto.scheduleRule;

import com.erick.nutricontrol.security.user.model.User;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRuleDetailDTO(
        Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, User admin) {}
