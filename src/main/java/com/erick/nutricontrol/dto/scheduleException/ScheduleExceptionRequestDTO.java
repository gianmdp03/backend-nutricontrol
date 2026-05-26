package com.erick.nutricontrol.dto.scheduleException;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleExceptionRequestDTO(
    @NotNull @FutureOrPresent LocalDate date,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @Size(max = 200) String reason) {}
