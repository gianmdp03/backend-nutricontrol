package com.erick.nutricontrol.dto.scheduleException;

import com.erick.nutricontrol.security.user.model.User;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleExceptionDetailDTO(
        Long id, LocalDate date, LocalTime startTime, LocalTime endTime, String reason, User admin) {}
