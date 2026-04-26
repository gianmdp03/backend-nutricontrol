package com.erick.nutricontrol.dto.scheduleRule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRuleUpdateDTO(
        DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Integer durationMinutes) {}
