package com.erick.nutricontrol.dto.scheduleRule;

import java.time.LocalTime;

public record ScheduleRuleUpdateDTO(
    String dayOfWeek, LocalTime startTime, LocalTime endTime, Integer durationMinutes) {}
