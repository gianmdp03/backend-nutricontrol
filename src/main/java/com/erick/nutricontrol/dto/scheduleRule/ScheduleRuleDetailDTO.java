package com.erick.nutricontrol.dto.scheduleRule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRuleDetailDTO(
        Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Integer durationMinutes) {}
