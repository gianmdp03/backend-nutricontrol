package com.erick.nutricontrol.dto.scheduleRule;

import java.time.LocalTime;

public record ScheduleRuleDetailDTO(
    Long id, String dayOfWeek, LocalTime startTime, LocalTime endTime, Integer durationMinutes) {}
