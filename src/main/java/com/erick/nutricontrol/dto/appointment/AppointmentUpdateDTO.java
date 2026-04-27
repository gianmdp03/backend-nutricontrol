package com.erick.nutricontrol.dto.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentUpdateDTO(LocalDate date, LocalTime startTime) {}
