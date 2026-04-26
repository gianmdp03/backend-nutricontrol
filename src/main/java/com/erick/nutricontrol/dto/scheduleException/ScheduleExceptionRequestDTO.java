package com.erick.nutricontrol.dto.scheduleException;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleExceptionRequestDTO(
    @NotNull @FutureOrPresent LocalDate date,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    String reason) {}
