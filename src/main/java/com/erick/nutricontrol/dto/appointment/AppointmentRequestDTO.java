package com.erick.nutricontrol.dto.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record AppointmentRequestDTO(
        @NotNull @FutureOrPresent OffsetDateTime startTime,
        @NotNull Long adminId) {}
