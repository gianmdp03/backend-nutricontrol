package com.erick.nutricontrol.dto.scheduleException;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleExceptionUpdateDTO(
    LocalDate date, LocalTime startTime, LocalTime endTime, @Size(max = 200) String reason) {}
