package com.erick.nutricontrol.dto.scheduleRule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ScheduleRuleRequestDTO(
    @NotBlank String dayOfWeek,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @NotNull Integer durationMinutes) {}
