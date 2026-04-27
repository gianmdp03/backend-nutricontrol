package com.erick.nutricontrol.dto.appointment;

import com.erick.nutricontrol._enum.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentDetailDTO(Long id, LocalDateTime date, AppointmentStatus appointmentStatus) {
}
