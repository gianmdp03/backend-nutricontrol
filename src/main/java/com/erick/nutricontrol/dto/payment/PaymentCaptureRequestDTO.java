package com.erick.nutricontrol.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentCaptureRequestDTO(@NotBlank String authorizationId) {}
