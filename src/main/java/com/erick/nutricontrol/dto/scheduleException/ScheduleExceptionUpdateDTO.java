package com.erick.nutricontrol.dto.scheduleException;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleExceptionUpdateDTO(
    LocalDate date, LocalTime startTime, LocalTime endTime, String reason) {}
