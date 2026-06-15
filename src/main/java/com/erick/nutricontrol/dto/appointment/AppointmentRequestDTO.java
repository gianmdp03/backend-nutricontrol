package com.erick.nutricontrol.dto.appointment;

import com.erick.nutricontrol._enum.AppointmentType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record AppointmentRequestDTO(
        @NotNull @FutureOrPresent OffsetDateTime startTime,
        @NotNull Long adminId,
        @NotNull AppointmentType appointmentType) {}
