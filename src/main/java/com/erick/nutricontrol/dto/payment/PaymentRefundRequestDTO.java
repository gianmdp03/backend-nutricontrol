package com.erick.nutricontrol.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentRefundRequestDTO(@NotBlank String captureId) {}
