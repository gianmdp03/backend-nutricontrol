package com.erick.nutricontrol.dto.appointment;

import java.time.OffsetDateTime;

public record AvailableSlotDTO(Long adminId, String adminName, OffsetDateTime startTimeUTC) {}
