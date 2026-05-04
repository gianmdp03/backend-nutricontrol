package com.erick.nutricontrol.dto.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO(@NotNull Long appointmentId) {
}
