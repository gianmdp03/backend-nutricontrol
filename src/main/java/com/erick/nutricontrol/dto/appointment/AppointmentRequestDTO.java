package com.erick.nutricontrol.dto.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequestDTO(
    @NotNull @FutureOrPresent LocalDate date,
    @NotNull @FutureOrPresent LocalTime startTime,
    @NotNull @FutureOrPresent LocalTime endTime,
    @NotNull Long adminId) {}
