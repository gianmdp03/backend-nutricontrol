package com.erick.nutricontrol.dto.appointment;

import com.erick.nutricontrol._enum.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentListDTO(Long id, LocalDateTime date, AppointmentStatus appointmentStatus) {
}
