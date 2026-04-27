package com.erick.nutricontrol.dto.appointment;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.security.user.dto.user.AdminDetailDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDetailDTO(
    Long id,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    AdminDetailDTO admin,
    AppointmentStatus appointmentStatus) {}
